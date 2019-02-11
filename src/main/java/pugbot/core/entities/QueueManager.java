package pugbot.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import pugbot.core.Database;
import pugbot.core.entities.timers.QueueFinishTimer;

public class QueueManager {
	private List<Queue> queueList = new ArrayList<>();
	private ConcurrentHashMap<QueueFinishTimer, Game> finishedGameMap = new ConcurrentHashMap<>();
	private Server server;

	public QueueManager(Server server) {
		this.server = server;
		
		Database.loadServerQueues(this);
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
			queue = getQueue(arg);
			
			if(queue != null && !queues.contains(queue)){
				queues.add(queue);
			}
		}
		
		return queues;
	}
	
	public Queue getQueue(String queueVal){
		Queue queue;
		
		try{
			queue = getQueueByIndex(Integer.valueOf(queueVal));
		}catch(NumberFormatException ex){
			queue = getQueueByName(queueVal);
		}
		
		return queue;
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
	 * @param players List of Member to be removed
	 */
	public void purgeQueue(List<Member> players) {
		for (Queue q : queueList) {
			q.purge(players);
		}
	}
	
	/**
	 * Removes a specified player from all queues.
	 * 
	 * @param player the player to be removed
	 */
	public void purgeQueue(Member player) {
		for (Queue q : queueList) {
			q.delete(player);
		}
	}
	
	/**
	 * Sets the topic of the server's pug channel and saves the queue to file.
	 */
	public void updateTopic() {
		Member self = getServer().getGuild().getSelfMember();
		TextChannel pugChannel = getServer().getPugChannel();
		
		if(self.hasPermission(pugChannel, Permission.MANAGE_CHANNEL)) {
			pugChannel.getManager().setTopic(getHeader()).queue();
		}
	}

	/**
	 * Returns a boolean based on if the player is in-game or not.
	 * 
	 * @param player the player to check
	 * @return true if the player is in-game, false if not
	 */
	public boolean isPlayerIngame(Member player) {
		for (Queue q : queueList) {
			for (Game g : q.getGames()) {
				if (g.getPlayers().contains(player)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Game getPlayersGame(Member player){
		for (Queue q : queueList) {
			for (Game g : q.getGames()) {
				if (g.getPlayers().contains(player)) {
					return g;
				}
			}
		}
		return null;
	}

	private void addToJustFinished(Game game) {
		QueueFinishTimer timer = new QueueFinishTimer(this);
		
		finishedGameMap.put(timer, game);
		timer.start();
	}
	
	public void queueFinishTimerEnd(QueueFinishTimer timer) {
		Game g = finishedGameMap.get(timer);
		
		for (Queue q : queueList) {
			q.addPlayersWaiting(g.getPlayers());
		}

		g.cleanup();
		finishedGameMap.remove(timer);
		updateTopic();
	}
	
	public int getWaitTimeRemaining(Member player){
		for(QueueFinishTimer timer : finishedGameMap.keySet()){
			Game g = finishedGameMap.get(timer);
			
			if(g.containsPlayer(player)){
				return timer.getTimeRemaining();
			}
		}
		
		return 0;
	}

	/**
	 * Checks if a player is currently in a queue or not.
	 * 
	 * @param player the player to be checked
	 * @return true if the player is in a queue, false of not
	 */
	public boolean isPlayerInQueue(Member player) {
		for (Queue q : queueList) {
			if (q.getPlayersInQueue().contains(player) || q.isPlayerInWaitlist(player)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPlayerJustFinished(Member player) {
		for(QueueFinishTimer timer : finishedGameMap.keySet()){
			Game g = finishedGameMap.get(timer);
			
			if(g.containsPlayer(player)){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Gets the id of the guild associated with this instance of QueueManager.
	 * 
	 * @return the guild id associated with this QueueManager instance
	 */
	public long getServerId() {
		return server.getId();
	}

	/**
	 * Gets the Server object associated with this instance of QueueManager.
	 * 
	 * @return the Server instance associated with this QueueManager instance
	 */
	public Server getServer() {
		return server;
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
	 * @param queueName the name of the queue to return
	 * @return Queue object matching the name param, null if no matches
	 */
	public Queue getQueueByName(String queueName){
		for(Queue q : queueList){
			if(q.getName().equalsIgnoreCase(queueName)){
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
	public Queue getQueueByIndex(Integer index){
		index--;
		
		if(doesQueueExist(index)){
			return queueList.get(index);
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
	public boolean isPlayerWaiting(Member player){
		for(Queue q : queueList){
			if(q.isPlayerInWaitlist(player)){
				return true;
			}
		}
		return false;
	}
	
	public void finishAllGames(){
		for(Queue queue : queueList){
			for(Game game : queue.getGames()){
				game.cleanup();
			}
		}
	}
	
	public void finishGame(Game game, Integer winningTeam){
		Queue queue = game.getParentQueue();
		
		game.finish();
		queue.removeGame(game);
		
		Database.updateGameInfo(game.getTimestamp(), queue.getId(), 
				getServerId(), System.currentTimeMillis(), winningTeam);
		
		if(winningTeam == null){
			game.cleanup();
			updateTopic();
			return;
		}
		
		addToJustFinished(game);
	}
}
