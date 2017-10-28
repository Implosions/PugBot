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

// QueueManager class; Is the interface between commands and queues on a server
public class QueueManager {
	private List<Queue> queueList = new ArrayList<Queue>();
	private List<User> justFinished = new ArrayList<User>();
	private String guildId;

	public QueueManager(String id) {
		guildId = id;
		loadFromFile();
	}

	/*
	 * Creates queue and adds it to queueList
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

	/*
	 * Adds user to each queue
	 * Adds user to queue's waitList instead if in justFinished
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
	
	/*
	 * Adds user to specified queue
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
	
	/*
	 * Adds user to specified queue
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
	
	/*
	 * Modifies existing queue
	 * Does not allow maxPlayers <= currentPlayers
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
	
	/*
	 * Removes queue
	 */
	public void removeQueue(Integer index) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			queueList.remove(queueList.get(i));
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeQueue(String name) {
		if (doesQueueExist(name)) {
			queueList.remove(getQueue(name));
		} else {
			throw new DoesNotExistException("Queue");
		}
	}
	
	public List<Queue> getQueueList() {
		return queueList;
	}

	public boolean isQueueListEmpty() {
		return queueList.isEmpty();
	}
	
	/*
	 * Returns basic queue information in the format <name> [<playercount>/<maxplayers>]...
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
	
	/*
	 * Finds game that contains player and finishes it
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

	/*
	 * Deletes player from all queues
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
	
	/*
	 * Deletes player from specified queue
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
	
	/*
	 * Purges list of players from all queues
	 */
	public void purgeQueue(List<User> players) {
		for (Queue q : queueList) {
			q.purge(players);
		}
	}
	
	/*
	 * Purges specified player from all queues
	 */
	public void purgeQueue(User player) {
		for (Queue q : queueList) {
			q.purge(player);
		}
	}
	
	/*
	 * Sets the topic of the server's pug channel and saves the queue to file
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

	/*
	 * Removes player from all queues
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

	/*
	 * Removes player from specified queue
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

	/*
	 * Removes player from specified queue
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

	/*
	 * Substitutes a player in-game with another player out-of-game
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

	public void addToJustFinished(List<User> players) {
		justFinished.addAll(players);
	}
	
	/*
	 * Adds players waiting during finish timer to respective queues
	 */
	public void timerEnd(List<User> players) {
		justFinished.removeAll(players);
		for (Queue q : queueList) {
			q.addPlayersWaiting(players);
		}
		System.out.println("Finish timer completed");
		updateTopic();
	}

	public boolean isPlayerInQueue(User player) {
		for (Queue q : queueList) {
			if (q.getPlayersInQueue().contains(player)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Adds a notification for the player in the specified queue at a designated playerCount
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

	/*
	 * Adds a notification for the player in the specified queue at a designated playerCount
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

	/*
	 * Removes all notifications
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
	
	/*
	 * Removes all notifications in a specified queue
	 */
	public void removeNotification(User user, Integer index) {
		Integer i = --index;
		if (doesQueueExist(i)) {
			queueList.get(i).removeNotification(user);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeNotification(User user, String name) {
		if (doesQueueExist(name)) {
			getQueue(name).removeNotification(user);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	/*
	 * Saves queues, current players in queue, and notifications to file
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

	/*
	 * Encodes queue objects to json
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

	/*
	 * Loads queue json from file
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

	/*
	 * Creates queues, adds players, and adds notifications from json
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

	public boolean hasPlayerJustFinished(User player) {
		return justFinished.contains(player);
	}

	public String getId() {
		return guildId;
	}

	public Server getServer() {
		return ServerManager.getServer(guildId);
	}

	public boolean doesQueueExist(String name) {
		for (Queue q : queueList) {
			if (q.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean doesQueueExist(Integer index) {
		if (index >= 0 && index < queueList.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	public Queue getQueue(String name){
		for(Queue q : queueList){
			if(q.getName().equalsIgnoreCase(name)){
				return q;
			}
		}
		return null;
	}
	
	public Queue getQueue(Integer index){
		return queueList.get(--index);
	}
	
	public boolean isPlayerWaiting(User player){
		for(Queue q : queueList){
			if(q.isPlayerWaiting(player)){
				return true;
			}
		}
		return false;
	}
}
