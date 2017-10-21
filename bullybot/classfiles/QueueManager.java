package bullybot.classfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import bullybot.errors.DuplicateEntryException;
import bullybot.errors.InvalidUseException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class QueueManager {
	private ArrayList<Queue> queueList = new ArrayList<Queue>();
	private HashMap<String, Integer> queueMap = new HashMap<String, Integer>();
	private ArrayList<User> justFinished = new ArrayList<User>();
	private String guildId;

	public QueueManager(String id) {
		guildId = id;
		loadFromFile();
	}

	public void create(String name, Integer players) {
		if (players > 0) {
			if (!queueMap.containsKey(name.toLowerCase())) {
				queueList.add(new Queue(name, players, guildId));
				queueMap.put(name.toLowerCase(), queueMap.size());
			} else {
				throw new DuplicateEntryException("A queue with the same name already exists");
			}
		} else {
			throw new BadArgumentsException("Player count must be greater than zero");
		}
	}

	public void addPlayer(User player) {
		if (!isQueueListEmpty()) {
			if (!isPlayerIngame(player)) {
				for (Queue q : queueList) {
					if (!q.isPlayerWaiting(player)) {
						if (!justFinished.contains(player)) {
							q.add(player);
							if (isPlayerIngame(player)) {
								return;
							}
						} else {
							q.addToWaitList(player);
						}
					} else {
						return;
					}
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void addPlayer(User player, String qName) {
		if (!isQueueListEmpty() && queueMap.containsKey(qName.toLowerCase())) {
			if (!isPlayerIngame(player)) {
				Queue q = queueList.get(queueMap.get(qName.toLowerCase()));
				if (!q.isPlayerWaiting(player)) {
					if (!justFinished.contains(player)) {
						q.add(player);
					} else {
						q.addToWaitList(player);
					}
				} else {
					return;
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void addPlayer(User player, Integer qIndex) {
		Integer index = --qIndex;
		if (!isQueueListEmpty() && index >= 0 && queueMap.containsValue(index)) {
			if (!isPlayerIngame(player)) {
				Queue q = queueList.get(index);
				if (!q.isPlayerWaiting(player)) {
					if (!justFinished.contains(player)) {
						q.add(player);
					} else {
						q.addToWaitList(player);
					}
				} else {
					return;
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void editQueue(Integer index, String newName, Integer maxPlayers) {
		Integer i = --index;
		if (!isQueueListEmpty() && i >= 0 && i < queueList.size()) {
			Queue q = queueList.get(i);
			if (q.getCurrentPlayers() < maxPlayers) {
				updateQueueMap(q.getName(), i, newName);
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
		if (!isQueueListEmpty() && queueMap.containsKey(oldName.toLowerCase())) {
			Integer i = queueMap.get(oldName.toLowerCase());
			Queue q = queueList.get(i);
			if (q.getCurrentPlayers() < maxPlayers) {
				updateQueueMap(oldName.toLowerCase(), i, newName);
				q.setName(newName);
				q.setMaxPlayers(maxPlayers);
			} else {
				throw new BadArgumentsException("New max players value must be lower than the old value");
			}

		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeQueue(Integer qIndex) {
		Integer index = --qIndex;
		System.out.println(String.valueOf(index));
		if (!isQueueListEmpty() && index < queueList.size() && index >= 0) {
			queueMap.remove(queueList.get(index).getName());
			queueList.remove(queueList.get(index));
			updateQueueMap(index);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeQueue(String qName) {
		if (!isQueueListEmpty() && queueMap.containsKey(qName)) {
			Integer i = queueMap.get(qName);
			queueList.remove(queueList.get(i));
			queueMap.remove(qName);
			updateQueueMap(i);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	private void updateQueueMap(Integer i) {
		for (Integer index : queueMap.values()) {
			if (index > i) {
				index--;
			}
		}
	}

	private void updateQueueMap(String oldName, Integer index, String newName) {
		queueMap.remove(oldName.toLowerCase());
		queueMap.put(newName.toLowerCase(), index);
	}

	public Queue getQueue(String name) {
		Integer index = queueMap.get(name.toLowerCase());
		if (index != null) {
			return queueList.get(index);
		} else {
			return null;
		}
	}

	public Queue getQueue(Integer index) {
		if (index > 0 && index <= queueList.size()) {
			return queueList.get(--index);
		} else {
			return null;
		}
	}

	public ArrayList<Queue> getQueue() {
		return queueList;
	}

	public boolean isQueueListEmpty() {
		if (queueList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public String getHeader() {
		if (isQueueListEmpty()) {
			return "**NO ACTIVE QUEUES**";
		} else {
			String header = "";
			for (Queue q : queueList) {
				String games = "";
				if (q.getNumberOfGames() > 0) {
					games = String.format(" (In game)");
				}
				header += String.format("%s [%d/%d]%s ", q.getName(), q.getCurrentPlayers(), q.getMaxPlayers(),
						games);
			}
			header = header.substring(0, header.lastIndexOf(" "));
			return header;
		}
	}

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

	public void deletePlayer(User player) {
		if (!isQueueListEmpty()) {
			if (!isPlayerIngame(player)) {
				for (Queue q : queueList) {
					if (q.getPlayersInQueue().contains(player)) {
						q.delete(player);
					}
				}
			} else {
				throw new InvalidUseException("You are already in-game");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void deletePlayer(User player, String qName) {
		if (queueMap.containsKey(qName.toLowerCase())) {
			if (!isPlayerIngame(player)) {
				queueList.get(queueMap.get(qName.toLowerCase())).delete(player);
			} else {
				throw new InvalidUseException("You are already in-game");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void deletePlayer(User player, Integer qIndex) {
		Integer index = --qIndex;
		if (queueMap.containsValue(index)) {
			if (!isPlayerIngame(player)) {
				queueList.get(index).delete(player);
			} else {
				throw new InvalidUseException("You are already in-game");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void purgeQueue(ArrayList<User> players) {
		for (Queue q : queueList) {
			q.purge(players);
		}
	}
	
	public void purgeQueue(User player){
		for (Queue q : queueList){
			q.purge(player);
		}
	}

	public void updateTopic() {
		if(getServer().getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL)){
			getServer().getPugChannel().getManager().setTopic(getHeader()).complete();
			System.out.println("Topic updated");
			saveToFile();
		}
	}

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

	public void remove(String name, Integer index) {
		Integer i = --index;
		if (!isQueueListEmpty() && i > 0 && i < queueList.size()) {
			Queue q = queueList.get(i);
			if (q.containsPlayer(name)) {
				q.delete(q.getPlayer(name));
			} else {
				return;
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void remove(String pName, String qName) {
		if (!isQueueListEmpty() && queueMap.containsKey(qName)) {
			Queue q = queueList.get(queueMap.get(qName));
			if (q.containsPlayer(pName)) {
				q.delete(q.getPlayer(pName));
			} else {
				return;
			}
		} else {
			throw new DoesNotExistException();
		}
	}

	public void sub(String target, String substitute) {
		if (!isQueueListEmpty()) {
			User sub = null;
			for (Member m : ServerManager.getServer(guildId).getGuild().getMembers()) {
				if (m.getUser().getName().equalsIgnoreCase(substitute)) {
					sub = m.getUser();
					break;
				}
			}
			if (sub == null) {
				throw new DoesNotExistException("Substitute player");
			} else if (isPlayerIngame(sub)) {
				throw new InvalidUseException("Player is already in-game");
			}
			for (Queue q : queueList) {
				for (Game g : q.getGames()) {
					if (g.containsPlayer(target)) {
						g.getPlayers().remove(g.getPlayer(target));
						g.getPlayers().add(sub);
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
		if (!isQueueListEmpty()) {
			for (Queue q : queueList) {
				for (Game g : q.getGames()) {
					if (g.getPlayers().contains(player)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void addToJustFinished(ArrayList<User> players) {
		justFinished.addAll(players);
	}

	public void timerEnd(ArrayList<User> players) {
		justFinished.removeAll(players);
		for (Queue q : queueList) {
			q.addPlayersWaiting(players);
		}
		System.out.println("Finish timer completed");
		updateTopic();
	}

	public boolean isPlayerInQueue(User player) {
		if (!isQueueListEmpty()) {
			for (Queue q : queueList) {
				if (q.getPlayersInQueue().contains(player)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addNotification(User player, Integer index, Integer playerCount) {
		Integer i = --index;
		if (!isQueueListEmpty() && i >= 0 && i < queueList.size()) {
			if (playerCount < queueList.get(i).getMaxPlayers()) {
				queueList.get(i).addNotification(player, playerCount);
			} else {
				throw new BadArgumentsException("Error! Number of players must be less than the max amount of players");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void addNotification(User player, String qName, Integer playerCount) {
		if (queueMap.containsKey(qName.toLowerCase())) {
			Queue q = queueList.get(queueMap.get(qName.toLowerCase()));
			if (playerCount < q.getMaxPlayers()) {
				q.addNotification(player, playerCount);
			} else {
				throw new BadArgumentsException("Error! Number of players must be less than the max amount of players");
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeNotifications(User user) {
		if (!isQueueListEmpty()) {
			for (Queue q : queueList) {
				q.removeNotification(user);
			}
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeNotifications(User user, Integer index) {
		Integer i = --index;
		if (!isQueueListEmpty() && i >= 0 && i < queueList.size()) {
			queueList.get(i).removeNotification(user);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void removeNotifications(User user, String qName) {
		if (queueMap.containsKey(qName)) {
			queueList.get(queueMap.get(qName)).removeNotification(user);
		} else {
			throw new DoesNotExistException("Queue");
		}
	}

	public void saveToFile() {
		try {
			System.out.println("Saving queue to file...");
			PrintWriter writer = new PrintWriter(new FileOutputStream(String.format("%s/%s/%s", "app_data", guildId, "queue.json")));
			writer.println(encodeJSON());
			writer.close();
			System.out.println("Queue saved");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String encodeJSON() {
		JSONObject root = new JSONObject();
		JSONArray ja = new JSONArray();
		for (Queue q : queueList) {
			JSONObject jQueue = new JSONObject();
			JSONArray jPlayers = new JSONArray();
			JSONArray jNotifications = new JSONArray();
			jQueue.put("name", q.getName());
			jQueue.put("maxplayers", q.getMaxPlayers());
			for (User p : q.getPlayersInQueue()) {
				jPlayers.put(p.getId());
			}
			jQueue.put("inqueue", jPlayers);

			q.getNotifications().forEach((i, ul) -> {
				JSONObject jNotification = new JSONObject();
				JSONArray jNotifyPlayers = new JSONArray();
				ul.forEach((u) -> jNotifyPlayers.put(u.getId()));
				jNotification.put("playercount", String.valueOf(i));
				jNotification.put("notifyplayers", jNotifyPlayers);
				jNotifications.put(jNotification);
			});
			jQueue.put("notifications", jNotifications);

			ja.put(jQueue);
		}
		root.put("queue", ja);
		return root.toString(4);
	}

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

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private void parseJSON(String input) {
		JSONObject json = new JSONObject(input);
		json.getJSONArray("queue").forEach((q) -> {
			JSONObject jq = new JSONObject(q.toString());
			create(jq.getString("name"), jq.getInt("maxplayers"));
			jq.getJSONArray("inqueue").forEach((p) -> {
				User player = ServerManager.getServer(guildId).getGuild().getMemberById(p.toString()).getUser();
				addPlayer(player, jq.getString("name"));
				ServerManager.getServer(guildId).updateActivityList(player);
			});
			jq.getJSONArray("notifications").forEach((ns) -> {
				JSONObject n = new JSONObject(ns.toString());
				n.getJSONArray("notifyplayers")
						.forEach((np) -> addNotification(
								ServerManager.getServer(guildId).getGuild().getMemberById(np.toString()).getUser(),
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
	
	public Server getServer(){
		return ServerManager.getServer(guildId);
	}
}
