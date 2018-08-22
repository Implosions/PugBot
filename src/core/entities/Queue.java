package core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import core.Database;
import core.entities.settings.QueueSettingsManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;

public class Queue {
	private Integer maxPlayers;
	private String name;
	private QueueManager manager;
	private int id;
	private List<Member> playersInQueue = new ArrayList<Member>();;
	private List<Game> games = new ArrayList<Game>();;
	private List<Member> waitlist = new ArrayList<Member>();
	private HashMap<Integer, List<Member>> notifications = new HashMap<Integer, List<Member>>();
	private QueueSettingsManager settingsManager;
	
	public Queue(String name, int maxPlayers, int id, QueueManager manager) {
		this.manager = manager;
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.id = id;
		this.settingsManager = new QueueSettingsManager(manager.getServer(), this);
	}

	/**
	 * Adds player to queue
	 * Checks to fire notifications
	 * Pops queue if at max capacity
	 * 
	 * @param player the player to be added
	 */
	public void addToQueue(Member player) {
		if (!playersInQueue.contains(player)) {
			playersInQueue.add(player);
			Database.insertPlayerInQueue(manager.getServerId(), id, player.getUser().getIdLong());
			checkNotifications();
				
			if (playersInQueue.size() == maxPlayers) {
				popQueue();
			}
		}
	}
	
	public void addPlayerToQueueDirectly(Member player){
		playersInQueue.add(player);
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
	public Integer getCurrentPlayersCount() {
		return playersInQueue.size();
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
	public List<Member> getPlayersInQueue() {
		return playersInQueue;
	}

	/**
	 * Creates a new game.
	 * Removes players in-game from other queues.
	 * Sends notification to the pug channel and to each player in queue.
	 */
	private void popQueue() {
		List<Member> players = new ArrayList<Member>(playersInQueue);
		
		// Create Game and add to the list of active games
		games.add(new Game(this, manager.getServerId(), players));

		manager.getServer().getQueueManager().purgeQueue(players);
		Database.deletePlayersInQueueFromQueue(manager.getServerId(), id);
	}
	
	protected void removeGame(Game game){
		games.remove(game);
	}

	/**
	 * Removes player from queue or waitList
	 * 
	 * @param member the player to remove
	 */
	public void delete(Member member) {
		if(playersInQueue.contains(member)){
			playersInQueue.remove(member);
			Database.deletePlayerInQueue(manager.getServerId(), id, member.getUser().getIdLong());
		}else if(waitlist.contains(member)){
			waitlist.remove(member);
		}
	}

	/**
	 * Removes all players in a list from queue
	 * 
	 * @param players List of players to remove from queue
	 */
	public void purge(List<Member> players) {
		for(Member player : players){
			if(playersInQueue.contains(player) || waitlist.contains(player)){
				delete(player);
			}
		}
	}

	/**
	 * Returns boolean based on if a player is matched to the provided name or not
	 * 
	 * @param name the name of the player to check for
	 * @return true if player matches the name provided
	 */
	public boolean containsPlayer(String name) {
		for (Member u : playersInQueue) {
			if (u.getEffectiveName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Randomizes players waiting after finishing a game into queue
	 * 
	 * @param players the players to allow to add to queue
	 */
	public void addPlayersWaiting(List<Member> players) {
		if(waitlist.size() > 0){
			Random random = new Random();
			List<Member> playersToAdd = new ArrayList<Member>(players);
			
			while(playersToAdd.size() > 0){
				Integer i = random.nextInt(playersToAdd.size());
				Member player = playersToAdd.get(i);
				
				if(waitlist.contains(player)){
					addToQueue(player);
					waitlist.remove(player);
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
	public void addToWaitList(Member player){
		if(!waitlist.contains(player)){
			waitlist.add(player);
		}
	}
	
	/**
	 * Checks if the specified player is in the wait list
	 * 
	 * @param player the player to check
	 * @return true if the player is in the wait list
	 */
	public boolean isPlayerInWaitlist(Member player){
		return waitlist.contains(player);
	}

	/**
	 * Adds a notification to alert a player if a playerCount threshold is met in this queue
	 * 
	 * @param player the player associated with the notification
	 * @param playerCount the threshold to alert to player
	 */
	public void addNotification(Member player, Integer playerCount) {
		if(notifications.containsKey(playerCount)){
			if(!notifications.get(playerCount).contains(player)){
				notifications.get(playerCount).add(player);
			}
		}else{
			notifications.put(playerCount, new ArrayList<Member>());
			notifications.get(playerCount).add(player);
		}
		Database.insertQueueNotification(manager.getServerId(), id, player.getUser().getIdLong(), playerCount);
	}
	
	/**
	 * Checks if notifications should be sent
	 */
	private void checkNotifications(){
		if(notifications.containsKey(playersInQueue.size())){
			notify(notifications.get(playersInQueue.size()));
		}
	}

	/**
	 * Sends alerts to the list of users that have notifications
	 * 
	 * @param users the users to send alerts to
	 */
	private void notify(List<Member> users) {
		for(Member m : users){
			if(!playersInQueue.contains(m) && (m.getOnlineStatus().equals(OnlineStatus.ONLINE) || m.getOnlineStatus().equals(OnlineStatus.IDLE))){
				try{
					m.getUser().openPrivateChannel().complete()
						.sendMessage(String.format("Queue: %s is at %d players!", name, playersInQueue.size())).complete();
				}catch(Exception ex){
					System.out.println("Error sending private message.\n" + ex.getMessage());
				}
			}
		}
	}

	/**
	 * Removes all notifications from this queue
	 * 
	 * @param player the player to remove notifications for
	 */
	public void removeNotification(Member player) {
		for(List<Member> list : notifications.values()){
			list.remove(player);
		}
		Database.deleteQueueNotification(manager.getServerId(), id, player.getUser().getIdLong());
	}
	
	/**
	 * Returns HashMap containing all notifications in this queue
	 * 
	 * @return HashMap of notifications in this queue
	 */
	public HashMap<Integer, List<Member>> getNotifications(){
		return notifications;
	}
	
	public int getId(){
		return id;
	}
	
	public QueueManager getManager(){
		return manager;
	}
	
	public QueueSettingsManager getSettingsManager(){
		return settingsManager;
	}
}
