package core.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.util.Functions;
import core.util.TimerTrigger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class Queue {
	private Integer maxPlayers;
	private Integer currentPlayers = 0;
	private String name;
	private String guildId;
	private List<User> playersInQueue = new ArrayList<User>();;
	private List<Game> games = new ArrayList<Game>();;
	private List<User> waitList = new ArrayList<User>();
	private HashMap<Integer, List<User>> notifications = new HashMap<Integer, List<User>>();
	private TimerTrigger t;
	
	public Queue(String name, Integer maxPlayers, String guildId) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.guildId = guildId;
	}

	/*
	 * Adds player to queue
	 * Checks to fire notifications
	 * Pops queue if at max capacity
	 */
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

	public List<Game> getGames() {
		return games;
	}

	public List<User> getPlayersInQueue() {
		return playersInQueue;
	}

	/*
	 * Creates a new game
	 * Removes players in-game from other queues
	 * Sends notification to the pug channel and to each player in queue
	 */
	private void popQueue() {
		String names = "";
		List<User> players = new ArrayList<User>(playersInQueue);
		Game newGame = new Game(guildId, name, players);
		games.add(newGame);
		
		for(User u : players){
			names += u.getName() + ", ";
			PrivateChannel c = u.openPrivateChannel().complete();
			c.sendMessage(String.format("`Your game: [%s] has started!`", name)).queue();
		}
		names = names.substring(0, names.lastIndexOf(","));
		
		ServerManager.getServer(guildId).getQueueManager().purgeQueue(players);
		String captainString = "";
		if(ServerManager.getServer(guildId).getSettings().randomizeCaptains()){
			captainString = String.format("%s%n**Captains:** <@%s> & <@%s>", names, newGame.getCaptains()[0], newGame.getCaptains()[1]);
		}
		ServerManager.getServer(guildId).getPugChannel().sendMessage(Functions.createMessage(String.format("Game: %s starting", name), captainString, Color.yellow)).queueAfter(2, TimeUnit.SECONDS);
	}

	/*
	 * Ends game, adds players to justFinished list, starts finish timer
	 */
	public void finish(Game g) {
		List<User> players = new ArrayList<User>(g.getPlayers());
		ServerManager.getServer(guildId).getQueueManager().addToJustFinished(players);
		games.remove(g);
		t = () -> ServerManager.getServer(guildId).getQueueManager().timerEnd(players);
		Timer timer = new Timer(ServerManager.getServer(guildId).getSettings().finishTime(), t);
		timer.start();
	}

	/*
	 * Removes player from queue or waitList
	 */
	public void delete(User s) {
		if(playersInQueue.contains(s)){
			playersInQueue.remove(s);
			currentPlayers--;
		}else if(waitList.contains(s)){
			waitList.remove(s);
		}
	}

	/*
	 * Removes all players in the list from queue
	 */
	public void purge(List<User> players) {
		playersInQueue.removeAll(players);
		waitList.removeAll(players);
		currentPlayers = playersInQueue.size();
	}
	/*
	 * Removes specified player from queue
	 */
	public void purge(User player){
		playersInQueue.remove(player);
		waitList.remove(player);
		currentPlayers = playersInQueue.size();
	}

	/*
	 * Returns true if player by String name is in queue
	 */
	public boolean containsPlayer(String name) {
		for (User u : playersInQueue) {
			if (u.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns user by String name
	 */
	public User getPlayer(String name) {
		for (User u : playersInQueue) {
			if (u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}

	/*
	 * Randomizes players waiting after finishing into queue
	 */
	public void addPlayersWaiting(List<User> players) {
		if(waitList.size() > 0){
			Random random = new Random();
			List<User> playersToAdd = new ArrayList<User>(players);
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

	/*
	 * Adds notification
	 */
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
	
	/*
	 * Determines if notifications should be sent
	 */
	private void checkNotifications(){
		if(notifications.containsKey(currentPlayers)){
			notify(notifications.get(currentPlayers));
		}
	}

	/*
	 * Sends notifications to the list of users
	 */
	private void notify(List<User> users) {
		for(User u : users){
			Member m = ServerManager.getServer(guildId).getGuild().getMemberById(u.getId());
			if(!playersInQueue.contains(u) && (m.getOnlineStatus().equals(OnlineStatus.ONLINE) || m.getOnlineStatus().equals(OnlineStatus.IDLE))){
				u.openPrivateChannel().complete().sendMessage(String.format("Queue: %s is at %d players!", name, currentPlayers)).complete();
			}
		}
	}

	public void removeNotification(User user) {
		for(List<User> list : notifications.values()){
			list.remove(user);
		}
	}
	
	public HashMap<Integer, List<User>> getNotifications(){
		return notifications;
	}
}
