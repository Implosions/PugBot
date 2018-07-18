package core.entities;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import core.util.Utils;
import core.Constants;
import core.Database;
import core.util.Trigger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

// Server class; Controls bot actions for each server
public class Server {
	private long id;
	private Guild guild;
	private ServerSettings settings;
	private QueueManager qm;
	private HashMap<User, Long> activityList = new HashMap<User, Long>();
	private java.util.Queue<Message> messageCache = new java.util.LinkedList<Message>();
	private List<String> banList;
	private List<String> adminList;
	
	public Commands cmds;

	public Server(Guild guild) {
		this.guild = guild;
		this.id = guild.getIdLong();
		cmds = new Commands(id);
		
		// Insert guild into database
		Database.insertDiscordServer(id, guild.getName());
		// Insert members into database
		for(Member m : guild.getMembers()){
			Database.insertPlayer(m.getUser().getIdLong(), m.getEffectiveName());
			Database.insertPlayerServer(id, m.getUser().getIdLong());
		}
		
		banList = Database.queryGetBanList(guild.getIdLong());
		adminList = Database.queryGetAdminList(guild.getIdLong());
		settings = new ServerSettings(guild.getIdLong());
		qm = new QueueManager(id);
		qm.getQueueList().forEach((q) -> q.getPlayersInQueue().forEach((u) -> updateActivityList(u)));
		
		startAFKTimer();
	}

	public long getid() {
		return id;
	}

	public QueueManager getQueueManager() {
		return qm;
	}

	public ServerSettings getSettings() {
		return settings;
	}

	public TextChannel getPugChannel() {
		for (TextChannel c : guild.getTextChannels()) {
			if (c.getName().equalsIgnoreCase(settings.getPUGChannel())) {
				return c;
			}
		}
		return guild.getDefaultChannel();
	}
	
	private void startAFKTimer() {
		Trigger tt = () -> afkTimerEnd();
		Timer afkTimer = new Timer(60, tt);
		afkTimer.start();
	}

	private void afkTimerEnd() {
		activityList.forEach((u, l) -> {
			if (qm.isPlayerInQueue(u) && (System.currentTimeMillis() - l) / 60000 >= settings.getAFKTimeout()) {
				qm.purgeQueue(u);
				String s = String.format("%s has been removed from the queue due to inactivity", u.getName());
				getPugChannel().sendMessage(Utils.createMessage("", s, Color.red)).queue();
				
				try{
					u.openPrivateChannel().complete()
						.sendMessage("You have been removed from the queue due to inactivity").queue();
				}catch(Exception ex){
					System.out.println("Error sending private message.\n" + ex.getMessage());
				}
				
				qm.updateTopic();
				System.out.println(s);
			}
		});
		startAFKTimer();
	}

	private void startDcTimer(Member m) {
		Trigger trigger = () -> dcTimerEnd(m);
		Timer timer = new Timer(settings.getDCTimeout() * 60, trigger);
		timer.start();
		System.out.println(String.format("User %s has gone offline, starting dc timer", m.getEffectiveName()));
	}

	private void dcTimerEnd(Member m) {
		if (m.getOnlineStatus().equals(OnlineStatus.OFFLINE) && qm.isPlayerInQueue(m.getUser())) {
			qm.purgeQueue(m.getUser());
			qm.updateTopic();
			String s = String.format("%s has been removed from queue after being offline for %s minutes", 
					m.getEffectiveName(), settings.getDCTimeout());
			getPugChannel().sendMessage(Utils.createMessage("", s,Color.red)).queue();
			System.out.println(s);
		}
	}
	
	public void updateActivityList(User u){
		if(qm.isPlayerInQueue(u) || qm.hasPlayerJustFinished(u)){
			activityList.put(u, System.currentTimeMillis());
		}else if(activityList.containsKey(u)){
			activityList.remove(u);
		}
	}
	
	public void playerDisconnect(Member m){
		if(qm.isPlayerInQueue(m.getUser()) || qm.hasPlayerJustFinished(m.getUser())){
			startDcTimer(m);
		}
	}
	
	public Guild getGuild(){
		return guild;
	}
	
	public void setGuild(Guild guild){
		this.guild = guild;
	}
	
	public boolean isAdmin(Member m){
		if(adminList.contains(m.getUser().getId()) 
				|| m.hasPermission(Permission.KICK_MEMBERS) 
				|| m.getUser().getId().equals(Constants.OWNER_ID)){
			return true;
		}
		return false;
	}
	
	public boolean isBanned(Member m){
		return banList.contains(m.getUser().getId());
	}
	
	public Member getMember(String player){
		for(Member m : guild.getMembers()){
			if(m.getEffectiveName().equalsIgnoreCase(player) || m.getUser().getId().equals(player)){
				return m;
			}
		}
		return null;
	}
	
	public void banUser(String playerId){
		if(!banList.contains(playerId)){
			banList.add(playerId);
		}
		Database.updateBanStatus(guild.getIdLong(), Long.valueOf(playerId), true);
	}
	
	public void unbanUser(String playerId){
		banList.remove(playerId);
		Database.updateBanStatus(id, Long.valueOf(playerId), false);
	}
	
	public void unbanAll(){
		for(String s : banList){
			Database.updateBanStatus(id, Long.valueOf(s), false);
		}
		banList.clear();
	}
	
	public void addAdmin(String playerId){
		if(!adminList.contains(playerId)){
			adminList.add(playerId);
		}
		Database.updateAdminStatus(id, Long.valueOf(playerId), true);
	}
	
	public void removeAdmin(String playerId){
		adminList.remove(playerId);
		Database.updateAdminStatus(id, Long.valueOf(playerId), false);
	}
	
	public boolean isSpam(Message message){
		boolean spam = false;
		for(Message m : messageCache){
			long timeDiff = message.getCreationTime().toEpochSecond() - m.getCreationTime().toEpochSecond();
			if(timeDiff <= 3 && m.getAuthor().getIdLong() == message.getAuthor().getIdLong() &&
					m.getContent().equals(message.getContent())){
				spam = true;
				break;
			}
		}
		
		if(messageCache.size() > 9){
			messageCache.remove();
		}
		messageCache.add(message);
		
		return spam;
	}
}
