package core.entities;

import java.util.ArrayList;
import java.util.List;

import core.Database;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class QueueManager {
	private List<Queue> queueList = new ArrayList<Queue>();
	private List<User> justFinished = new ArrayList<User>();
	private long serverId;

	public QueueManager(long id) {
		serverId = id;

		queueList = Database.getServerQueueList(id);
	}

	public void addQueue(Queue queue){
		queueList.add(queue);
	}
	
	public void removeQueue(Queue queue){
		queueList.remove(queue);
	}
	
	/**
	 * Returns the queue list.
	 * 
	 * @return List containing all the queues
	 */
	public List<Queue> getQueueList() {
		return queueList;
	}
	
	public List<Queue> getListOfQueuesFromStringArgs(String[] args){
		List<Queue> queues = new ArrayList<Queue>();
		Queue queue;
		for(String arg : args){
			try{
				queue = getQueue(Integer.valueOf(arg));
			}catch(NumberFormatException ex){
				queue = getQueue(arg);
			}
			
			if(queue != null){
				queues.add(queue);
			}
		}
		
		return queues;
	}

	/**
	 * Returns a boolean result based on if the queue list is empty or not.
	 * 
	 * @return returns true if empty, false if not empty
	 */
	public boolean isQueueListEmpty() {
		return queueList.isEmpty();
	}
	
	/**
	 * Returns a String containing compiled basic queue information.
	 * 
	 * @return String containing compiled basic queue information
	 */
	public String getHeader() {
		if (isQueueListEmpty()) {
			return "NO ACTIVE QUEUES";
		} else {
			String header = "";
			for (Queue q : queueList) {
				String games = "";
				if (q.getNumberOfGames() > 0) {
					games = String.format(" (In game)");
				}
				header += String.format("%s [%d/%d]%s ", q.getName(), q.getCurrentPlayersCount(), q.getMaxPlayers(), games);
			}
			header = header.substring(0, header.lastIndexOf(" "));
			return header;
		}
	}
	
	/**
	 * Removes a list of players from all queues
	 * 
	 * @param players List of User to be removed
	 */
	public void purgeQueue(List<User> players) {
		for (Queue q : queueList) {
			q.purge(players);
		}
	}
	
	/**
	 * Removes a specified player from all queues.
	 * 
	 * @param player the player to be removed
	 */
	public void purgeQueue(User player) {
		for (Queue q : queueList) {
			q.delete(player);
		}
	}
	
	/**
	 * Sets the topic of the server's pug channel and saves the queue to file.
	 */
	public void updateTopic() {
		try {
			getServer().getPugChannel().getManager().setTopic(getHeader()).complete();
			System.out.println("Topic updated");
		} catch (PermissionException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns a boolean based on if the player is in-game or not.
	 * 
	 * @param player the player to check
	 * @return true if the player is in-game, false if not
	 */
	public boolean isPlayerIngame(User player) {
		for (Queue q : queueList) {
			for (Game g : q.getGames()) {
				if (g.getPlayers().contains(player)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Game getPlayersGame(User player){
		for (Queue q : queueList) {
			for (Game g : q.getGames()) {
				if (g.getPlayers().contains(player)) {
					return g;
				}
			}
		}
		return null;
	}

	/**
	 * Adds a list of players to justFinished.
	 * 
	 * @param players the players to be added to the list
	 */
	public void addToJustFinished(List<User> players) {
		justFinished.addAll(players);
	}
	
	/**
	 * Removes a List of players from justFinished after the finish timer ends and.
	 * then adds them if they are waiting to join a queue.
	 * 
	 * @param players the players to be removed from justFinished and added to their respective queues
	 */
	public void timerEnd(List<User> players) {
		justFinished.removeAll(players);
		for (Queue q : queueList) {
			q.addPlayersWaiting(players);
		}
		System.out.println("Finish timer completed");
		updateTopic();
	}

	/**
	 * Checks if a player is currently in a queue or not.
	 * 
	 * @param player the player to be checked
	 * @return true if the player is in a queue, false of not
	 */
	public boolean isPlayerInQueue(User player) {
		for (Queue q : queueList) {
			if (q.getPlayersInQueue().contains(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns boolean based on if the player has just finished a game or not.
	 * 
	 * @param player the player to check
	 * @return true if the player has just finished, false if not
	 */
	public boolean hasPlayerJustFinished(User player) {
		return justFinished.contains(player);
	}

	/**
	 * Gets the id of the guild associated with this instance of QueueManager.
	 * 
	 * @return the guild id associated with this QueueManager instance
	 */
	public long getId() {
		return serverId;
	}

	/**
	 * Gets the Server object associated with this instance of QueueManager.
	 * 
	 * @return the Server instance associated with this QueueManager instance
	 */
	public Server getServer() {
		return ServerManager.getServer(serverId);
	}

	
	/**
	 * Checks if the specified queue exists.
	 * 
	 * @param name the name of the queue to check
	 * @return true if the queue exists
	 */
	public boolean doesQueueExist(String name) {
		for (Queue q : queueList) {
			if (q.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the specified queue exists.
	 * 
	 * @param index the one-based index of the queue to check
	 * @return true if the queue exists
	 */
	public boolean doesQueueExist(Integer index) {
		if (index >= 0 && index < queueList.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns a specific queue.
	 * 
	 * @param name the name of the queue to return
	 * @return Queue object matching the name param, null if no matches
	 */
	public Queue getQueue(String name){
		for(Queue q : queueList){
			if(q.getName().equalsIgnoreCase(name)){
				return q;
			}
		}
		return null;
	}
	
	/**
	 * Returns a specific queue.
	 * 
	 * @param index the one-based index of the queue to return
	 * @return Queue object at the specified index, null if no matches
	 */
	public Queue getQueue(Integer index){
		Integer i = --index;
		if(doesQueueExist(i)){
			return queueList.get(i);
		}else{
			return null;
		}
	}
	
	/**
	 * Returns boolean based on if the player is in a queue's wait list.
	 * 
	 * @param player the player to check
	 * @return true if the player is in a queue's wait list
	 * @see Queue
	 */
	public boolean isPlayerWaiting(User player){
		for(Queue q : queueList){
			if(q.isPlayerWaiting(player)){
				return true;
			}
		}
		return false;
	}
	
	public void finishAllGames(){
		for(Queue queue : queueList){
			for(Game game : queue.getGames()){
				game.finish();
			}
		}
	}
}
