package bullybot.classfiles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import bullybot.classfiles.functions.Stuff;
import bullybot.classfiles.functions.TimerTrigger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class Queue {
	private Integer maxPlayers;
	private Integer currentPlayers;
	private String name;
	private String guildId;
	private ArrayList<User> playersInQueue;
	private ArrayList<Game> games;
	private ArrayList<User> waitList = new ArrayList<User>();
	private HashMap<Integer,ArrayList<User>> notifications = new HashMap<Integer,ArrayList<User>>();
	private TimerTrigger t;
	
	public Queue(String name, Integer maxPlayers, String guildId) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.guildId = guildId;
		this.currentPlayers = 0;
		this.playersInQueue = new ArrayList<User>();
		this.games = new ArrayList<Game>();
	}

	public void add(User name) {
		if (!playersInQueue.contains(name)) {
			playersInQueue.add(name);
			currentPlayers++;
			checkNotifications();
			if (currentPlayers == maxPlayers) {
				popQueue();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(Integer num) {
		maxPlayers = num;
	}

	public Integer getCurrentPlayers() {
		return currentPlayers;
	}

	public Integer getNumberOfGames() {
		return games.size();
	}

	public ArrayList<Game> getGames() {
		return games;
	}

	public ArrayList<User> getPlayersInQueue() {
		return playersInQueue;
	}

	private void popQueue() {
		String names = "";
		ArrayList<User> players = new ArrayList<User>(playersInQueue);
		Game newGame = new Game(name, players);
		games.add(newGame);
		
		for(User u : players){
			names += u.getName() + ", ";
			PrivateChannel c = u.openPrivateChannel().complete();
			c.sendMessage(String.format("`Your game: [%s] has started!`", name)).queue();
		}
		names = names.substring(0, names.lastIndexOf(","));
		
		PugManager.getQueueManager(guildId).purgeQueue(players);
		PugManager.getPugChannel(guildId).sendMessage(Stuff.createMessage(String.format("Game: [%s] starting", name), String.format("%s%n**Captains:** <@%s> & <@%s>", names, newGame.getCaptains()[0], newGame.getCaptains()[1]), Color.cyan)).queueAfter(2, TimeUnit.SECONDS);
	}

	public void finish(Game g) {
		ArrayList<User> players = new ArrayList<User>(g.getPlayers());
		PugManager.getQueueManager(guildId).addToJustFinished(players);
		games.remove(g);
		t = () -> PugManager.getQueueManager(guildId).timerEnd(players);
		Timer timer = new Timer(60, t);
		timer.start();
	}

	public void delete(User s) {
		if(playersInQueue.contains(s)){
			playersInQueue.remove(s);
			currentPlayers--;
		}
	}

	public void purge(ArrayList<User> players) {
		playersInQueue.removeAll(players);
		waitList.removeAll(players);
		currentPlayers = playersInQueue.size();
	}
	
	public void purge(User player){
		playersInQueue.remove(player);
		waitList.remove(player);
		currentPlayers = playersInQueue.size();
	}

	public boolean containsPlayer(String name) {
		for (User u : playersInQueue) {
			if (u.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public User getPlayer(String name) {
		for (User u : playersInQueue) {
			if (u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}

	public void addPlayersWaiting(ArrayList<User> players) {
		if(waitList.size() > 0){
			Random random = new Random();
			ArrayList<User> playersToAdd = new ArrayList<User>(players);
			while(playersToAdd.size() > 0){
				Integer i = random.nextInt(playersToAdd.size());
				User player = playersToAdd.get(i);
				if(waitList.contains(player)){
					add(player);
					waitList.remove(player);
				}
				playersToAdd.remove(player);
			}
		}
	}
	
	public void addToWaitList(User player){
		if(!waitList.contains(player)){
			waitList.add(player);
		}
	}
	
	public boolean isPlayerWaiting(User player){
		return waitList.contains(player);
	}

	public void addNotification(User player, Integer playerCount) {
		if(notifications.containsKey(playerCount)){
			if(!notifications.get(playerCount).contains(player)){
				notifications.get(playerCount).add(player);
			}
		}else{
			notifications.put(playerCount, new ArrayList<User>());
			notifications.get(playerCount).add(player);
		}
	}
	
	private void checkNotifications(){
		if(notifications.containsKey(currentPlayers)){
			notify(notifications.get(currentPlayers));
		}
	}

	private void notify(ArrayList<User> users) {
		for(User u : users){
			Member m = PugManager.getGuild(guildId).getMemberById(u.getId());
			if(!playersInQueue.contains(u) && (m.getOnlineStatus().equals(OnlineStatus.ONLINE) || m.getOnlineStatus().equals(OnlineStatus.IDLE))){
				u.openPrivateChannel().complete().sendMessage(String.format("Queue: %s is at %d players!", name, currentPlayers)).complete();
			}
		}
	}

	public void removeNotification(User user) {
		for(ArrayList<User> list : notifications.values()){
			list.remove(user);
		}
	}
	
	public HashMap<Integer,ArrayList<User>> getNotifications(){
		return notifications;
	}
}
