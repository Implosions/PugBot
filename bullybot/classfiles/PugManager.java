package bullybot.classfiles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bullybot.classfiles.functions.Stuff;
import bullybot.classfiles.functions.TimerTrigger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PugManager {
	private static HashMap<String, QueueManager> queueMap;
	private ArrayList<User> dcList;
	private static HashMap<User, Long> activityList;
	private static JDA jda;
	
	public PugManager(JDA jdaInstance){
		queueMap = new HashMap<String, QueueManager>();
		dcList = new ArrayList<User>();
		activityList = new HashMap<User, Long>();
		jda = jdaInstance;
		jda.getGuilds().forEach((g) -> queueMap.put(g.getId(), new QueueManager(g.getId())));
		
		TimerTrigger t = () -> afkTimerEnd();
		Timer afkTimer = new Timer(60, t);
		afkTimer.start();
	}
	
	public static QueueManager getQueueManager(String id){
		return queueMap.get(id);
	}
	
	public static TextChannel getPugChannel(String id){
		for(TextChannel c : jda.getGuildById(id).getTextChannels()){
			if (c.getName().equalsIgnoreCase(Info.PUG_CHANNEL)){
				return c;
			}
		}
		return jda.getGuildById(id).getDefaultChannel();
	}
	
	public ArrayList<User> getDcList(){
		return dcList;
	}
	
	public void startDcTimer(User player){
		TimerTrigger trigger = () -> dcTimerEnd(player);
		dcList.add(player);
		Timer timer = new Timer(120, trigger);
		timer.start();
		System.out.println(String.format("User %s has gone offline, starting dc timer", player.getName()));
	}

	private void dcTimerEnd(User player) {
		if(dcList.contains(player)){
			queueMap.forEach((id, qm) -> {if(qm.isPlayerInQueue(player)){
			qm.deletePlayer(player);
			qm.updateTopic();
			getPugChannel(id).sendMessage(Stuff.createMessage("", String.format("%s has been removed from queue after being offline for 2 minutes", player.getName()), Color.red)).queue();
			}});
			dcList.remove(player);
		}
	}
	
	private void afkTimerEnd(){
		queueMap.forEach((id, qm) -> activityList.forEach((u, l) -> {if(qm.isPlayerInQueue(u) && (System.currentTimeMillis() - l) / 60000 >= 120){qm.deletePlayer(u);
		String s = String.format("%s has been removed from the queue due to inactivity", u.getName());
		getPugChannel(id).sendMessage(Stuff.createMessage("", s, Color.red)).queue();
		u.openPrivateChannel().complete().sendMessage("You have been removed from the queue due to inactivity").queue();
		qm.updateTopic();
		System.out.println(s);
		};}));
		
		TimerTrigger t = () -> afkTimerEnd();
		Timer afkTimer = new Timer(60, t);
		afkTimer.start();
	}
	
	public static void updateActivityList(User u){
		for(QueueManager qm : queueMap.values()){
			if(qm.isPlayerInQueue(u) || qm.hasPlayerJustFinished(u)){
				activityList.put(u, System.currentTimeMillis());
			}
		}
	}
	
	public static List<Member> getMembers(String id){
		return jda.getGuildById(id).getMembers();
	}
	
	public static Guild getGuild(String id){
		return jda.getGuildById(id);
	}
	
	public static void addNewServer(String id){
		TimerTrigger tt = () -> queueMap.put(id, new QueueManager(id));
		Timer t = new Timer(5, tt);
		t.start();
	}
	
	public static void removeServer(String id){
		queueMap.remove(id);
	}
	
	public static HashMap<User, Long> getActivityList(){
		return activityList;
	}
}
