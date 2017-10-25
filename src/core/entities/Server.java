package core.entities;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;

import core.util.Functions;
import core.util.TimerTrigger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Server {
	private String id;
	private Guild guild;
	private Settings settings;
	private QueueManager qm;
	public Commands cmds;
	private HashMap<User, Long> activityList = new HashMap<User, Long>();

	public Server(Guild guild) {
		this.guild = guild;
		this.id = guild.getId();
		Functions.createDir(String.format("%s/%s", "app_data", id));
		qm = new QueueManager(id);
		cmds = new Commands();
		settings = new Settings(id);
		qm.getQueue().forEach((q) -> q.getPlayersInQueue().forEach((u) -> updateActivityList(u)));
		startAFKTimer();
	}

	public String getid() {
		return id;
	}

	public QueueManager getQueueManager() {
		return qm;
	}

	public Settings getSettings() {
		return settings;
	}

	public TextChannel getPugChannel() {
		for (TextChannel c : guild.getTextChannels()) {
			if (c.getName().equalsIgnoreCase(settings.pugChannel())) {
				return c;
			}
		}
		return guild.getDefaultChannel();
	}

	private void startAFKTimer() {
		TimerTrigger tt = () -> afkTimerEnd();
		Timer afkTimer = new Timer(60, tt);
		afkTimer.start();
	}

	private void afkTimerEnd() {
		activityList.forEach((u, l) -> {
			if (qm.isPlayerInQueue(u) && (System.currentTimeMillis() - l) / 60000 >= settings.afkTime()) {
				qm.deletePlayer(u);
				String s = String.format("%s has been removed from the queue due to inactivity", u.getName());
				getPugChannel().sendMessage(Functions.createMessage("", s, Color.red)).queue();
				u.openPrivateChannel().complete().sendMessage("You have been removed from the queue due to inactivity")
						.queue();
				qm.updateTopic();
				System.out.println(s);
			}
		});

		startAFKTimer();
	}

	private void startDcTimer(Member m) {
		TimerTrigger trigger = () -> dcTimerEnd(m);
		Timer timer = new Timer(settings.dcTime(), trigger);
		timer.start();
		System.out.println(String.format("User %s has gone offline, starting dc timer", m.getEffectiveName()));
	}

	private void dcTimerEnd(Member m) {
		if (m.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
			qm.deletePlayer(m.getUser());
			qm.updateTopic();
			String s = String.format("%s has been removed from queue after being offline for %s minutes", m.getEffectiveName(), new DecimalFormat("#.##").format((double)settings.dcTime()/60));
			getPugChannel().sendMessage(Functions.createMessage("", s,Color.red)).queue();
			System.out.println(s);
		}else{
			System.out.println(String.format("%s has returned", m.getEffectiveName()));
		}
	}
	
	public void updateActivityList(User u){
		if(qm.isPlayerInQueue(u) || qm.hasPlayerJustFinished(u)){
			activityList.put(u, System.currentTimeMillis());
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
}
