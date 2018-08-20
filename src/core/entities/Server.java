package core.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import core.util.Utils;
import core.Constants;
import core.Database;
import core.exceptions.InvalidUseException;
import core.util.Trigger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.managers.RoleManager;

// Server class; Controls bot actions for each server
public class Server {
	private Guild guild;
	private ServerSettings settings;
	private QueueManager qm;
	private HashMap<Member, Long> activityList = new HashMap<Member, Long>();
	private java.util.Queue<Message> messageCache = new java.util.LinkedList<Message>();
	private Set<Long> banList;
	private Set<Long> adminList;
	private HashMap<String, Role> groupDict = new HashMap<String, Role>();

	public Commands cmds;

	public Server(Guild guild) {
		this.guild = guild;
		cmds = new Commands(this);

		// Insert guild into database
		Database.insertDiscordServer(getId(), guild.getName());
		// Insert members into database
		for (Member m : guild.getMembers()) {
			Database.insertPlayer(m.getUser().getIdLong(), m.getEffectiveName());
			Database.insertPlayerServer(guild.getIdLong(), m.getUser().getIdLong());
		}

		banList = Database.queryGetBanList(guild.getIdLong());
		adminList = Database.queryGetAdminList(guild.getIdLong());
		settings = new ServerSettings(guild.getIdLong());
		qm = new QueueManager(guild.getIdLong());
		qm.getQueueList().forEach((q) -> q.getPlayersInQueue().forEach((u) -> updateActivityList(u)));
		groupDict = Database.retrieveGroups(guild.getIdLong());

		startAFKTimer();
	}

	public long getId() {
		return guild.getIdLong();
	}

	public QueueManager getQueueManager() {
		return qm;
	}

	public ServerSettings getSettings() {
		return settings;
	}

	public TextChannel getPugChannel() {
		List<TextChannel> channels = guild.getTextChannelsByName(settings.getPUGChannel(), false);

		if (channels.isEmpty()) {
			return guild.getDefaultChannel();
		}

		return channels.get(0);
	}

	private void startAFKTimer() {
		Trigger tt = () -> afkTimerEnd();
		Timer afkTimer = new Timer(60, tt);
		afkTimer.start();
	}

	private void afkTimerEnd() {
		boolean update = false;
		
		for (Member member : activityList.keySet()) {
			long time = activityList.get(member);

			if (!qm.isPlayerInQueue(member)) {
				activityList.remove(member);
				continue;
			}

			if ((System.currentTimeMillis() - time) / 60000 >= settings.getAFKTimeout()) {
				String msg = String.format("<%d> has been removed from all queues after being inactive for %d minutes",
						member.getUser().getId(), settings.getAFKTimeout());

				getPugChannel().sendMessage(Utils.createMessage("", msg, false)).queue();
				
				update = true;
			}
		}
		
		if(update){
			qm.updateTopic();
		}
		
		startAFKTimer();
	}

	private void startDcTimer(Member m) {
		Trigger trigger = () -> dcTimerEnd(m);
		Timer timer = new Timer(settings.getDCTimeout() * 60, trigger);
		timer.start();
	}

	private void dcTimerEnd(Member m) {
		if (m.getOnlineStatus().equals(OnlineStatus.OFFLINE) && qm.isPlayerInQueue(m)) {
			qm.purgeQueue(m);
			qm.updateTopic();

			String msg = String.format("%s has been removed from all queues after being offline for %s minutes",
					m.getEffectiveName(), settings.getDCTimeout());

			getPugChannel().sendMessage(Utils.createMessage("", msg, false)).queue();
		}
	}

	public void updateActivityList(Member m) {
		if (qm.isPlayerInQueue(m) || qm.hasPlayerJustFinished(m)) {
			activityList.put(m, System.currentTimeMillis());
		} else if (activityList.containsKey(m)) {
			activityList.remove(m);
		}
	}

	public void playerDisconnect(Member m) {
		if (qm.isPlayerInQueue(m) || qm.hasPlayerJustFinished(m)) {
			startDcTimer(m);
		}
	}

	public Guild getGuild() {
		return guild;
	}

	public boolean isAdmin(Member m) {
		if (adminList.contains(m.getUser().getId()) || m.hasPermission(Permission.KICK_MEMBERS)
				|| m.getUser().getId().equals(Constants.OWNER_ID)) {
			return true;
		}
		return false;
	}

	public boolean isBanned(Member m) {
		return banList.contains(m.getUser().getId());
	}

	public Member getMember(String member) {
		for (Member m : guild.getMembers()) {
			if (m.getEffectiveName().equalsIgnoreCase(member) || m.getUser().getId().equals(member)) {
				return m;
			}
		}
		throw new InvalidUseException("Member does not exist");
	}

	public void banUser(long userId) {
		if (banList.contains(userId)) {
			throw new InvalidUseException("This user is already banned");
		}

		banList.add(userId);
		Database.updateBanStatus(guild.getIdLong(), Long.valueOf(userId), true);
	}

	public void unbanUser(long userId) {
		if (!banList.contains(userId)) {
			throw new InvalidUseException("This user is not banned");
		}

		banList.remove(userId);
		Database.updateBanStatus(getId(), Long.valueOf(userId), false);
	}

	public void unbanAll() {
		for (long userId : banList) {
			Database.updateBanStatus(getId(), userId, false);
		}

		banList.clear();
	}

	public void addAdmin(long userId) {
		if (adminList.contains(userId)) {
			throw new InvalidUseException("User is already an admin");
		}

		adminList.add(userId);
		Database.updateAdminStatus(getId(), Long.valueOf(userId), true);
	}

	public void removeAdmin(long userId) {
		if (!adminList.contains(userId)) {
			throw new InvalidUseException("User is not an admin");
		}

		adminList.remove(userId);
		Database.updateAdminStatus(getId(), Long.valueOf(userId), false);
	}

	/**
	 * Checks a message cache to see if the message sent is identical to any
	 * other within 3 seconds
	 * 
	 * @param message
	 * @return
	 */
	public boolean isSpam(Message message) {
		boolean spam = false;
		for (Message m : messageCache) {
			long timeDiff = message.getCreationTime().toEpochSecond() - m.getCreationTime().toEpochSecond();
			if (timeDiff <= 3 && m.getAuthor().getIdLong() == message.getAuthor().getIdLong()
					&& m.getContent().equals(message.getContent())) {
				spam = true;
				break;
			}
		}

		if (messageCache.size() > 9) {
			messageCache.remove();
		}
		messageCache.add(message);

		return spam;
	}

	/**
	 * Creates a role, adds it to the group dictionary, and inserts the id into
	 * the database
	 * 
	 * @throws InvalidUseException
	 *             If a group with the same name already exists
	 * 
	 * @param groupName
	 *            The name of the group
	 */
	public void addGroup(String groupName) {
		if (groupDict.containsKey(groupName)) {
			throw new InvalidUseException("A group with this name already exists!");
		}

		Role role = guild.getController().createRole().complete();
		RoleManager manager = role.getManager();

		// TODO: Update with new syntax after JDA upgrade
		manager.setMentionable(true).queue();
		manager.setName(groupName).queue();

		groupDict.put(groupName.toLowerCase(), role);
		Database.insertGroup(getId(), role.getIdLong());
	}

	/**
	 * Deletes a specified group and associated role
	 * 
	 * @throws InvalidUseException
	 *             If groupName is not a valid group
	 * 
	 * @param groupName
	 *            The name of the group
	 */
	public void deleteGroup(String groupName) {
		if (!groupDict.containsKey(groupName)) {
			throw new InvalidUseException("A group with that name does not exist");
		} else {
			Role role = groupDict.get(groupName);
			groupDict.remove(groupName);
			Database.deleteGroup(getId(), role.getIdLong());
			role.delete().queue();
		}
	}

	/**
	 * Creates an array of all group names
	 * 
	 * @return A String[] containing all group names
	 */
	public String[] getGroupNames() {
		return groupDict.keySet().toArray(new String[0]);
	}

	/**
	 * Gets a group by its name
	 * 
	 * @param groupName
	 *            The name of the group
	 * @return The Role associated with the group
	 */
	public Role getGroup(String groupName) {
		if (!groupDict.containsKey(groupName)) {
			throw new InvalidUseException(String.format("The group %s does not exist", groupName));
		}

		return groupDict.get(groupName.toLowerCase());
	}
}
