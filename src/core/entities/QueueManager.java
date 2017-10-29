package core.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.exceptions.DuplicateEntryException;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class QueueManager {
	private List<Queue> queueList = new ArrayList<Queue>();
	private List<User> justFinished = new ArrayList<User>();
	private String guildId;

	public QueueManager(String id) {
		guildId = id;
		loadFromFile();
	}

	/**
	 * Creates queue and adds it to queueList.
	 * 
	 * @param name the name of the queue.
	 * @param players the amount of players the queue will hold.
	 * @see Queue
	 */
	public void createQueue(String name, Integer players) {
		if (players > 0) {
			if (!doesQueueExist(name)) {
				queueList.add(new Queue(name, players, guildId));
			} else {
				throw new DuplicateEntryException("A queue with the same name already exists");
			}
		} else {
			throw new BadArgumentsException("Player count must be greater than zero");
		}
	}

	/**
	 * Adds the player to each queue, if the player has just finished, adds to each queue's waitlist instead.
	 * 
	 * @param player the player being added
	 * @see Queue
	 */
	public void addPlayerToQueue(User player) {
		if (!isQueueListEmpty()) {
			if (!isPlayerIngame(player)) {
				for (Queue q : queueList) {
					if (!justFinished.contains(player)) {
						q.add(player);
						// If queue pops return
						if (isPlayerIngame(player)) {
							return;
						}
					} else {
						q.addToWaitList(player);
					}
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Adds the player to a specific queue, if the player has just finished, adds to that queue's waitlist instead.
	 * 
	 * @param player the player being added
	 * @param name the name of the queue
	 * @see Queue
	 */
	public void addPlayerToQueue(User player, String name) {
		if (doesQueueExist(name)) {
			if (!isPlayerIngame(player)) {
				Queue q = getQueue(name);
				if (!justFinished.contains(player)) {
					q.add(player);
				} else {
					q.addToWaitList(player);
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Adds the player to a specific queue, if the player has just finished, adds to that queue's waitlist instead.
	 * 
	 * @param player the player being added
	 * @param index the one-based index of the queue
	 * @see Queue
	 */
	public void addPlayerToQueue(User player, Integer index) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			if (!isPlayerIngame(player)) {
				Queue q = queueList.get(i);
				if (!justFinished.contains(player)) {
					q.add(player);
				} else {
					q.addToWaitList(player);
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Modifies an existing queue with a new name and max players.
	 * 
	 * @param index the one-based index of the queue to be modified
	 * @param newName the new name of the queue
	 * @param maxPlayers the new amount of players the queue will hold
	 * @see Queue
	 */
	public void editQueue(Integer index, String newName, Integer maxPlayers) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			Queue q = queueList.get(i);
			if (q.getCurrentPlayers() < maxPlayers) {
				q.setName(newName);
				q.setMaxPlayers(maxPlayers);
			} else {
				throw new BadArgumentsException("New max players value must be lower than the old value");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Modifies an existing queue with a new name and max players.
	 * 
	 * @param oldName the old name of the queue to be modified
	 * @param newName the new name of the queue
	 * @param maxPlayers the new amount of players the queue will hold
	 * @see Queue
	 */
	public void editQueue(String oldName, String newName, Integer maxPlayers) {
		if (doesQueueExist(oldName)) {
			Queue q = getQueue(oldName);
			if (q.getCurrentPlayers() < maxPlayers) {
				q.setName(newName);
				q.setMaxPlayers(maxPlayers);
			} else {
				throw new BadArgumentsException("New max players value must be lower than the old value");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Removes a queue from the queue list
	 * 
	 * @param index the one-based index of the queue to be removed
	 * @see Queue
	 */
	public void removeQueue(Integer index) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			queueList.remove(queueList.get(i));
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Removes a queue from the queue list.
	 * 
	 * @param name the name of the queue to be removed
	 * @see Queue
	 */
	public void removeQueue(String name) {
		if (doesQueueExist(name)) {
			queueList.remove(getQueue(name));
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Returns the queue list.
	 * 
	 * @return List containing all the queues
	 */
	public List<Queue> getQueueList() {
		return queueList;
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
				header += String.format("%s [%d/%d]%s ", q.getName(), q.getCurrentPlayers(), q.getMaxPlayers(), games);
			}
			header = header.substring(0, header.lastIndexOf(" "));
			return header;
		}
	}
	
	/**
	 * Finds the game the player is in and ends it.
	 * 
	 * @param player the user ending the game
	 */
	public void finish(User player) {
		if (isPlayerIngame(player)) {
			for (Queue q : queueList) {
				for (Game g : q.getGames()) {
					if (g.getPlayers().contains(player)) {
						q.finish(g);
						return;
					}
				}
			}
		} else {
			throw new InvalidUseException("You are not in-game");
		}
	}

	/**
	 * Deletes the player from all queues.
	 * 
	 * @param player the player to be deleted
	 */
	public void deletePlayer(User player) {
		if (!isQueueListEmpty()) {
			if (!isPlayerIngame(player)) {
				for (Queue q : queueList) {
					System.out.println("test");
					q.delete(player);
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Deletes the player from a specific queue.
	 * 
	 * @param player the player to be deleted
	 * @param name the name of the queue that the player will be deleted from
	 */
	public void deletePlayer(User player, String name) {
		if (doesQueueExist(name)) {
			if (!isPlayerIngame(player)) {
				getQueue(name).delete(player);
			} else {
				throw new InvalidUseException("You are already in-game");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Deletes the player from a specific queue.
	 * 
	 * @param player the player to be deleted
	 * @param index the one-based index of the queue that the player will be deleted from
	 */
	public void deletePlayer(User player, Integer index) {
		Integer i = index;
		if (doesQueueExist(i)) {
			if (!isPlayerIngame(player)) {
				queueList.get(i).delete(player);
			} else {
				throw new InvalidUseException("You are already in-game");
			}
		} else {
			throw new DoesNotExistException("Queue");
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
			q.purge(player);
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
		saveToFile();
	}

	/**
	 * Removes a player from all queues.
	 * 
	 * @param name the name of the player to be removed
	 */
	public void remove(String name) {
		if (!isQueueListEmpty()) {
			boolean found = false;
			for (Queue q : queueList) {
				if (q.containsPlayer(name)) {
					q.delete(q.getPlayer(name));
					found = true;
				}
			}
			if (!found) {
				throw new InvalidUseException(name + " not found in queue");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Removes a player from a specific queues.
	 * 
	 * @param name the name of the player to be removed
	 * @param index the one-based index of the queue that the player will be removed from
	 */
	public void remove(String name, Integer index) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			Queue q = queueList.get(i);
			if (q.containsPlayer(name)) {
				q.delete(q.getPlayer(name));
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Removes a player from a specific queues.
	 * 
	 * @param playerName the name of the player to be removed
	 * @param queueName the name of the queue that the player will be removed from
	 */
	public void remove(String playerName, String queueName) {
		if (doesQueueExist(queueName)) {
			Queue q = getQueue(queueName);
			if (q.containsPlayer(playerName)) {
				q.delete(q.getPlayer(playerName));
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Substitutes a player in-game with another player out-of-game.
	 * 
	 * @param target the player that will be substituted
	 * @param substitute the player that will replace the target
	 */
	public void sub(String target, String substitute) {
		if (!isQueueListEmpty()) {
			User sub = null;
			// Match member object to substitute name
			for (Member m : ServerManager.getServer(guildId).getGuild().getMembers()) {
				if (m.getEffectiveName().equalsIgnoreCase(substitute)) {
					sub = m.getUser();
					break;
				}
			}
			if (sub == null) {
				throw new DoesNotExistException("Substitute player");
			} else if (isPlayerIngame(sub)) {
				throw new InvalidUseException(sub.getName() + " is already in-game");
			}
			for (Queue q : queueList) {
				for (Game g : q.getGames()) {
					if (g.containsPlayer(target)) {
						g.sub(g.getPlayer(target), sub);
						purgeQueue(sub);
						return;
					}
				}
			}
			throw new InvalidUseException(target + " is not in-game");
		} else {
			throw new DoesNotExistException("Queue");
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
	 * Adds a notification to a queue.
	 * A notification sends a message to the user when it has reached the specified threshold.
	 * 
	 * @param player the player the notification will be associated with
	 * @param index the one-based index of the queue the player will be notified for
	 * @param playerCount the threshold amount of players that will trigger the notification
	 */
	public void addNotification(User player, Integer index, Integer playerCount) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			Queue q = queueList.get(i);
			if (playerCount < q.getMaxPlayers()) {
				q.addNotification(player, playerCount);
			} else {
				throw new BadArgumentsException("Error! Number of players must be less than the max amount of players");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Adds a notification to a queue.
	 * A notification sends a message to the user when it has reached the specified threshold.
	 * 
	 * @param player the player the notification will be associated with
	 * @param name the name of the queue the player will be notified for
	 * @param playerCount the threshold amount of players that will trigger the notification
	 */
	public void addNotification(User player, String name, Integer playerCount) {
		if (doesQueueExist(name)) {
			Queue q = getQueue(name);
			if (playerCount < q.getMaxPlayers()) {
				q.addNotification(player, playerCount);
			} else {
				throw new BadArgumentsException("Error! Number of players must be less than the max amount of players");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Removes all notifications associated with the user.
	 * 
	 * @param user the user associated with the notifications
	 */
	public void removeNotification(User user) {
		if (!isQueueListEmpty()) {
			for (Queue q : queueList) {
				q.removeNotification(user);
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	/**
	 * Removes all notifications associated with the user in a specific queue.
	 * 
	 * @param user the user associated with the notifications
	 * @param index the one-based index of the queue
	 */
	public void removeNotification(User user, Integer index) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			queueList.get(i).removeNotification(user);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Removes all notifications associated with the user in a specific queue.
	 * 
	 * @param user the user associated with the notifications
	 * @param name the name of the queue
	 */
	public void removeNotification(User user, String name) {
		if (doesQueueExist(name)) {
			getQueue(name).removeNotification(user);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/**
	 * Saves a json format to file representing the queues, players in queue, and notifications.
	 */
	public void saveToFile() {
		try {
			System.out.println("Saving queue to file...");
			PrintWriter writer = new PrintWriter(new FileOutputStream(String.format("%s/%s/%s", "app_data", guildId, "queue.json")));
			writer.println(getJSON());
			writer.close();
			System.out.println("Queue saved");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns a String containing queue information encoded as json.
	 * 
	 * @return a String containing queue information encoded as json.
	 */
	private String getJSON() {
		JSONObject root = new JSONObject();
		JSONArray ja = new JSONArray();
		
		for (Queue q : queueList) {
			JSONObject jQueue = new JSONObject();
			JSONArray jPlayers = new JSONArray();
			JSONArray jNotifications = new JSONArray();
			// Adds name and max players
			jQueue.put("name", q.getName());
			jQueue.put("maxplayers", q.getMaxPlayers());
			// Adds players
			for (User p : q.getPlayersInQueue()) {
				jPlayers.put(p.getId());
			}
			jQueue.put("inqueue", jPlayers);
			// Encodes notifications
			q.getNotifications().forEach((i, ul) -> {
				JSONObject jNotification = new JSONObject();
				JSONArray jNotifyPlayers = new JSONArray();
				
				ul.forEach((u) -> jNotifyPlayers.put(u.getId()));
				jNotification.put("playercount", String.valueOf(i));
				jNotification.put("notifyplayers", jNotifyPlayers);
				jNotifications.put(jNotification);
			});
			jQueue.put("notifications", jNotifications);
			// Adds queue to json array
			ja.put(jQueue);
		}
		root.put("queue", ja);
		return root.toString(4);
	}

	/**
	 * Loads the server's queue information from a json file.
	 */
	private void loadFromFile() {
		String s = String.format("%s/%s/%s", "app_data", guildId, "queue.json");
		if (new File(s).exists()) {
			try {
				System.out.println("Loading queue from file...");
				Scanner reader = new Scanner(new FileInputStream(s));
				String input = "";

				while (reader.hasNextLine()) {
					input += reader.nextLine();
				}
				reader.close();

				if (!input.isEmpty()) {
					parseJSON(input);
					System.out.println("Queue loaded");
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * Decodes json representing a previous queue state.
	 * Creates queues, adds players, and adds notifications.
	 * 
	 * @param input the json to be decoded
	 */
	private void parseJSON(String input) {
		JSONObject json = new JSONObject(input);
		json.getJSONArray("queue").forEach((q) -> {
			JSONObject jq = new JSONObject(q.toString());
			// Creates queue
			createQueue(jq.getString("name"), jq.getInt("maxplayers"));
			jq.getJSONArray("inqueue").forEach((p) -> {
				// Adds each player
				User player = ServerManager.getGuild(guildId).getMemberById(p.toString()).getUser();
				addPlayerToQueue(player, jq.getString("name"));
			});
			// Creates notifications
			jq.getJSONArray("notifications").forEach((ns) -> {
				JSONObject n = new JSONObject(ns.toString());
				n.getJSONArray("notifyplayers")
						.forEach((np) -> addNotification(
								ServerManager.getGuild(guildId).getMemberById(np.toString()).getUser(),
								jq.getString("name"), n.getInt("playercount")));
			});
		});
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
	public String getId() {
		return guildId;
	}

	/**
	 * Gets the Server object associated with this instance of QueueManager.
	 * 
	 * @return the Server instance associated with this QueueManager instance
	 */
	public Server getServer() {
		return ServerManager.getServer(guildId);
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
		if(doesQueueExist(--index)){
			return queueList.get(--index);
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
}
