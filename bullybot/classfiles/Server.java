package bullybot.classfiles;

import java.awt.Color;
import java.util.HashMap;

import bullybot.classfiles.util.Functions;
import bullybot.classfiles.util.TimerTrigger;
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
	private HashMap<User, Long> activityList;

	public Server(String id, Guild g) {
		Functions.createFile(String.format("%s/%s", "app_data", id));
		this.id = id;
		guild = g;
		qm = new QueueManager(id);
		settings = new Settings(id);

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
			if (c.getName().equalsIgnoreCase(settings.getPugChannelName())) {
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
			if (qm.isPlayerInQueue(u) && (System.currentTimeMillis() - l) / 60000 >= 120) {
				qm.deletePlayer(u);
				String s = String.format("%s has been removed from the queue due to inactivity", u.getName());
				getPugChannel().sendMessage(Functions.createMessage("", s, Color.red)).queue();
				u.openPrivateChannel().complete().sendMessage("You have been removed from the queue due to inactivity")
						.queue();
				qm.updateTopic();
				System.out.println(s);
			}
			;
		});

		startAFKTimer();
	}

	private void startDcTimer(Member m) {
		TimerTrigger trigger = () -> dcTimerEnd(m);
		Timer timer = new Timer(120, trigger);
		timer.start();
		System.out.println(String.format("User %s has gone offline, starting dc timer", m.getEffectiveName()));
	}

	private void dcTimerEnd(Member m) {
		if (m.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
			qm.deletePlayer(m.getUser());
			qm.updateTopic();
			String s = String.format("%s has been removed from queue after being offline for 2 minutes", m.getEffectiveName());
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
}
