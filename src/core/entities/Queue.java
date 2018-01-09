package core.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.util.Utils;
import core.entities.Game.GameStatus;
import core.util.Trigger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
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
	private Trigger t;
	
	public Queue(String name, Integer maxPlayers, String guildId) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.guildId = guildId;
	}

	/**
	 * Adds player to queue
	 * Checks to fire notifications
	 * Pops queue if at max capacity
	 * 
	 * @param player the player to be added
	 */
	public void add(User player) {
		if (!playersInQueue.contains(player)) {
			playersInQueue.add(player);
			currentPlayers++;
			checkNotifications();
			if (currentPlayers == maxPlayers) {
				popQueue();
			}
		}
	}

	/**
	 * Returns the name of the queue
	 * 
	 * @return the name of the queue
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the queue
	 * 
	 * @param s the new queue name
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * Returns the max capacity of the queue
	 * 
	 * @return the number of max players the queue can hold
	 */
	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Sets the max capacity of the queue
	 * 
	 * @param num the new maxPlayers value
	 */
	public void setMaxPlayers(Integer num) {
		maxPlayers = num;
	}

	/**
	 * Returns the current number of players in queue
	 * 
	 * @return
	 */
	public Integer getCurrentPlayers() {
		return currentPlayers;
	}

	/**
	 * Get the amount of currently active games for this queue
	 * 
	 * @return the number of active games
	 */
	public Integer getNumberOfGames() {
		return games.size();
	}

	/**
	 * Returns the List of active games for this queue
	 * 
	 * @return List containing the active games
	 */
	public List<Game> getGames() {
		return games;
	}

	/**
	 * Returns List of players currently in queue
	 * 
	 * @return List of players currently in queue
	 */
	public List<User> getPlayersInQueue() {
		return playersInQueue;
	}

	/**
	 * Creates a new game.
	 * Removes players in-game from other queues.
	 * Sends notification to the pug channel and to each player in queue.
	 */
	private void popQueue() {
		String names = "";
		List<User> players = new ArrayList<User>(playersInQueue);
		TextChannel pugChannel = ServerManager.getServer(guildId).getPugChannel();
		
		// Send alert to players and compile their names
		for(User u : players){
			names += u.getName() + ", ";
			PrivateChannel c = u.openPrivateChannel().complete();
			c.sendMessage(String.format("`Your game: %s has started!`", name)).queue();
		}
		names = names.substring(0, names.lastIndexOf(","));
		
		// Create Game and add to the list of active games
		Game newGame = new Game(guildId, name, players);
		games.add(newGame);
		
		// Remove players from all other queues
		ServerManager.getServer(guildId).getQueueManager().purgeQueue(players);
		
		// Generate captain string
		String captainString = "";
		if(ServerManager.getServer(guildId).getSettings().randomizeCaptains()){
			captainString = String.format("**Captains:** <@%s> & <@%s>", newGame.getCaptains()[0].getId(), newGame.getCaptains()[1].getId());
		}
		
		// Send game start message to pug channel
		pugChannel.sendMessage(Utils.createMessage(String.format("Game: %s starting%n", name), String.format("%s%n%s", names, captainString), Color.YELLOW)).queueAfter(2, TimeUnit.SECONDS);
		
		/*String servers = new CmdPugServers().getServers(guildId, null);
		if(!servers.equals("N/A")){
			pugChannel.sendMessage(Utils.createMessage("`Pug servers:`", servers, true)).queueAfter(2, TimeUnit.SECONDS);
		}*/
	}

	/**
	 * Ends game, adds players to justFinished list, starts finish timer
	 * 
	 * @param g the game to finish
	 */
	public void finish(Game g) {
		List<User> players = new ArrayList<User>(g.getPlayers());
		ServerManager.getServer(guildId).getQueueManager().addToJustFinished(players);
		if(g.getStatus() == GameStatus.PICKING){
			g.removeMenus();
		}
		games.remove(g);
		t = () -> ServerManager.getServer(guildId).getQueueManager().timerEnd(players);
		Timer timer = new Timer(ServerManager.getServer(guildId).getSettings().finishTime(), t);
		timer.start();
	}

	/**
	 * Removes player from queue or waitList
	 * 
	 * @param s the player to remove
	 */
	public void delete(User s) {
		if(playersInQueue.contains(s)){
			playersInQueue.remove(s);
			currentPlayers--;
		}else if(waitList.contains(s)){
			waitList.remove(s);
		}
	}

	/**
	 * Removes all players in a list from queue
	 * 
	 * @param players List of players to remove from queue
	 */
	public void purge(List<User> players) {
		playersInQueue.removeAll(players);
		waitList.removeAll(players);
		currentPlayers = playersInQueue.size();
	}
	/**
	 * Removes a specific player from queue
	 * 
	 * @param player the player to remove from queue
	 */
	public void purge(User player){
		playersInQueue.remove(player);
		waitList.remove(player);
		currentPlayers = playersInQueue.size();
	}

	/**
	 * Returns boolean based on if a player is matched to the provided name or not
	 * 
	 * @param name the name of the player to check for
	 * @return true if player matches the name provided
	 */
	public boolean containsPlayer(String name) {
		for (User u : playersInQueue) {
			if (u.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a User object matching the provided name
	 * 
	 * @param name the name to match to a player
	 * @return User object matching the provided name, null if no matches
	 */
	public User getPlayer(String name) {
		for (User u : playersInQueue) {
			if (u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}

	/**
	 * Randomizes players waiting after finishing a game into queue
	 * 
	 * @param players the players to allow to add to queue
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
	
	/**
	 * Adds a player that has just finished to the wait list
	 * 
	 * @param player the player to add to the wait list
	 */
	public void addToWaitList(User player){
		if(!waitList.contains(player)){
			waitList.add(player);
		}
	}
	
	/**
	 * Checks if the specified player is in the wait list
	 * 
	 * @param player the player to check
	 * @return true if the player is in the wait list
	 */
	public boolean isPlayerWaiting(User player){
		return waitList.contains(player);
	}

	/**
	 * Adds a notification to alert a player if a playerCount threshold is met in this queue
	 * 
	 * @param player the player associated with the notification
	 * @param playerCount the threshold to alert to player
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
	
	/**
	 * Checks if notifications should be sent
	 */
	private void checkNotifications(){
		if(notifications.containsKey(currentPlayers)){
			notify(notifications.get(currentPlayers));
		}
	}

	/**
	 * Sends alerts to the list of users that have notifications
	 * 
	 * @param users the users to send alerts to
	 */
	private void notify(List<User> users) {
		for(User u : users){
			Member m = ServerManager.getServer(guildId).getGuild().getMemberById(u.getId());
			if(!playersInQueue.contains(u) && (m.getOnlineStatus().equals(OnlineStatus.ONLINE) || m.getOnlineStatus().equals(OnlineStatus.IDLE))){
				u.openPrivateChannel().complete().sendMessage(String.format("Queue: %s is at %d players!", name, currentPlayers)).complete();
			}
		}
	}

	/**
	 * Removes all notifications from this queue
	 * 
	 * @param player the player to remove notifications for
	 */
	public void removeNotification(User player) {
		for(List<User> list : notifications.values()){
			list.remove(player);
		}
	}
	
	/**
	 * Returns HashMap containing all notifications in this queue
	 * 
	 * @return HashMap of notifications in this queue
	 */
	public HashMap<Integer, List<User>> getNotifications(){
		return notifications;
	}
}
