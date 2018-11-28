package core.entities;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import core.util.Utils;
import core.Constants;
import core.Database;
import core.commands.ICommand;
import core.entities.settings.ServerSettingsManager;
import core.entities.timers.DCTimer;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.RoleManager;

// Server class; Controls bot actions for each server
public class Server {
	
	private Guild guild;
	private ServerSettingsManager settingsManager;
	private QueueManager queueManager;
	private ConcurrentHashMap<Member, Long> activityList = new ConcurrentHashMap<Member, Long>();
	private ConcurrentHashMap<Member, DCTimer> disconnectList = new ConcurrentHashMap<>();
	private java.util.Queue<Message> messageCache = new LinkedList<Message>();
	private List<Long> banList;
	private List<Long> adminList;
	private HashMap<String, Role> groupDict = new HashMap<String, Role>();
	private CommandManager commandManager;

	public Server(Guild guild) {
		this.guild = guild;
		commandManager = new CommandManager(this);

		// Insert guild into database
		Database.insertDiscordServer(getId(), guild.getName());
		// Insert members into database
		for (Member m : guild.getMembers()) {
			Database.insertPlayer(m.getUser().getIdLong(), m.getEffectiveName());
			Database.insertPlayerServer(guild.getIdLong(), m.getUser().getIdLong());
		}
		
		settingsManager = new ServerSettingsManager(this);
		banList = Database.queryGetBanList(guild.getIdLong());
		adminList = Database.queryGetAdminList(guild.getIdLong());
		queueManager = new QueueManager(this);
		groupDict = Database.retrieveGroups(guild.getIdLong());
		
		queueManager.getQueueList().forEach((q) -> q.getPlayersInQueue().forEach((u) -> updateActivityList(u)));
	}
	
	public void processMessage(Message message) {
		String content = message.getContent();
		Member caller = guild.getMember(message.getAuthor());
		
		if (!content.startsWith("!") || caller.getUser().isBot()) {
			return;
		}
		
		if(isBanned(caller) || isSpam(message)) {
			return;
		}
		
		// Workaround for users with spaces in their name
		// Replaces name with user id
		if (message.getMentionedUsers().size() > 0) {
			for (User u : message.getMentionedUsers()) {
				content = content.replace("@" + guild.getMember(u).getEffectiveName(), u.getId());
			}
		}

		// Replaces standard emote string
		// Allows bot to use server specific emotes
		if (message.getEmotes().size() > 0) {
			HashSet<Emote> emotes = new HashSet<Emote>(message.getEmotes());
			
			for (Emote emote : emotes) {
				content = content.replace(":" + emote.getName() + ":",
						String.format("<:%s:%s>", emote.getName(), emote.getId()));
			}
		}

		// Remove command prefix and extra spaces
		content = content.substring(1);
		content = content.trim().replaceAll(" +", " ");
		
		String[] args = content.split(" ");
		
		processCommand(caller, message.getTextChannel(), args);
		updateActivityList(caller);
	}
	
	private void processCommand(Member caller, TextChannel channel, String[] args) {
		String commandName = args[0];
		String[] commandArgs = Arrays.copyOfRange(args, 1,args.length);
		Message response = null;
		ICommand cmd = null;

		try{
			// Check if command is valid
			if(!commandManager.doesCommandExist(commandName) || !commandManager.isCommandEnabled(commandName)) {
				return;
			}
			
			cmd = commandManager.getCommand(commandName);
			
			// Check if command is in the correct channel
			if(!cmd.isGlobalCommand() && channel != getPugChannel()){
				return;
			}
			
			// Check if admin is required
			if (cmd.isAdminRequired() && !isAdmin(caller)){
				throw new InvalidUseException("Admin is required for this command");
			}
			
			// Log input
			Date date = new Date(System.currentTimeMillis());
			String logString = String.format("%tc [%s][%s][%s]%s", 
					date, guild.getName(), channel.getName(),
					caller.getUser().toString(), Arrays.toString(args));
			
			System.out.println(logString);
			
			// Execute command
			response = cmd.execCommand(caller, commandArgs);
			System.out.println(String.format("Command %s executed", cmd.getName()));
			
		} catch(InvalidUseException ex) {
			response = Utils.createMessage("Error!", ex.getMessage(), false);
		} catch (BadArgumentsException ex) {
			response = Utils.createMessage("Error!",
					String.format("%s%nUsage:%n%s", ex.getMessage(), cmd.getHelp()), false);
		} catch (Exception ex) {
			response = Utils.createMessage("Error!", "Something went wrong", false);
			ex.printStackTrace();
		}
		
		if(response != null) {
			channel.sendMessage(response).queue();
		}
	}

	public long getId() {
		return guild.getIdLong();
	}

	public QueueManager getQueueManager() {
		return queueManager;
	}

	public ServerSettingsManager getSettingsManager() {
		return settingsManager;
	}

	public TextChannel getPugChannel() {
		TextChannel channel = settingsManager.getPUGChannel(); 

		if(channel == null){
			channel = guild.getDefaultChannel();
		}

		return channel;
	}

	protected void checkActivityList() {
		boolean update = false;
		
		for (Member member : activityList.keySet()) {
			long time = activityList.get(member);

			if (!queueManager.isPlayerInQueue(member)) {
				activityList.remove(member);
				continue;
			}
			
			long timeDiffMs = System.currentTimeMillis() - time;
			long minutes = TimeUnit.MINUTES.convert(timeDiffMs, TimeUnit.MILLISECONDS);
			
			if (minutes >= settingsManager.getAFKTimeout()) {
				String msg = String.format("<@%s> has been removed from all queues after being inactive for %d minutes",
						member.getUser().getId(), settingsManager.getAFKTimeout());
				

				queueManager.purgeQueue(member);
				getPugChannel().sendMessage(Utils.createMessage("", msg, false)).queue();
				
				update = true;
			}
		}
		
		if(update){
			queueManager.updateTopic();
		}
	}

	public void dcTimerEnd(Member member) {
		if (member.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
			queueManager.purgeQueue(member);
			queueManager.updateTopic();

			String msg = String.format("%s has been removed from all queues after being offline for %s minutes",
					member.getEffectiveName(), settingsManager.getDCTimeout());

			getPugChannel().sendMessage(Utils.createMessage("", msg, false)).queue();
		}
		
		disconnectList.remove(member);
	}

	public void updateActivityList(Member m) {
		if (queueManager.isPlayerInQueue(m)) {
			activityList.put(m, System.currentTimeMillis());
		} else if (activityList.containsKey(m)) {
			activityList.remove(m);
		}
	}

	public void playerDisconnect(Member member) {
		if (queueManager.isPlayerInQueue(member)) {
			DCTimer timer = new DCTimer(this, member);
			
			if(disconnectList.containsKey(member)){
				disconnectList.get(member).interrupt();
			}
			
			disconnectList.put(member, timer);
			timer.start();
		}
	}

	public Guild getGuild() {
		return guild;
	}

	public boolean isAdmin(Member m) {
		if (adminList.contains(m.getUser().getIdLong()) || m.hasPermission(Permission.KICK_MEMBERS)
				|| m.getUser().getId().equals(Constants.OWNER_ID)) {
			return true;
		}
		return false;
	}

	public boolean isBanned(Member m) {
		return banList.contains(m.getUser().getIdLong());
	}

	public Member getMember(String memberString) {
		Member member = null;
		long id;
		
		try {
			id = Long.valueOf(memberString);
			
			member = guild.getMemberById(id);
		}catch(NumberFormatException ex) {
			member = getMemberByName(memberString);
		}
		
		if(member != null) {
			return member;
		}
		
		throw new InvalidUseException("Member does not exist");
	}
	
	private Member getMemberByName(String name) {
		List<Member> members = guild.getMembersByEffectiveName(name, true);
		
		if(members.size() > 0) {
			return members.get(0);
		}
		
		return null;
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
	
	public CommandManager getCommandManager(){
		return commandManager;
	}
}
