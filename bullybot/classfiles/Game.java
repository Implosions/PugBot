package bullybot.classfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bullybot.classfiles.util.Functions;
import net.dv8tion.jda.core.entities.User;

public class Game {
	private String guildId;
	private String name;
	private Long timestamp;
	private ArrayList<User> players;
	private String[] captains = new String[] { "", "" };

	public Game(String id, String name, ArrayList<User> players) {
		this.guildId = id;
		this.name = name;
		this.timestamp = System.currentTimeMillis();
		this.players = new ArrayList<User>(players);
		if (ServerManager.getServer(guildId).getSettings().randomizeCaptains()) {
			randomizeCaptains();
		}
		logGame();
	}

	public ArrayList<User> getPlayers() {
		return players;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public User getPlayer(String name) {
		for (User u : players) {
			if (u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}

	public boolean containsPlayer(String name) {
		for (User u : players) {
			if (u.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	private void randomizeCaptains() {
		Random random = new Random();
		List<User> captainPool = getCaptainPool();
		if (players.size() == 1) {
			captains[0] = captainPool.get(0).getId();
			captains[1] = captainPool.get(0).getId();
			return;
		}
		captains[0] = captainPool.get(random.nextInt(captainPool.size())).getId();
		while (captains[1].isEmpty()) {
			Integer i = random.nextInt(captainPool.size());
			if (!captainPool.get(i).getId().equals(captains[0])) {
				captains[1] = captainPool.get(i).getId();
			}
		}
	}

	public String[] getCaptains() {
		return captains;
	}

	public String getName() {
		return name;
	}

	private List<User> getCaptainPool() {
		List<User> captainPool = new ArrayList<User>();
		String games = loadGames();
		Integer minGames = ServerManager.getServer(guildId).getSettings().minNumberOfGames();
		if (games != null) {
			for (User u : players) {
				Integer count = 0;
				Matcher m = Pattern.compile(u.getId()).matcher(games);
				while (m.find()) {
					count++;
				}
				if (count >= minGames) {
					captainPool.add(u);
				}
			}
			if (captainPool.size() >= 2) {
				return captainPool;
			}
		}
		return players;
	}

	private void logGame() {
		String s = String.format("%s/%s/%s", "app_data", guildId, "games.txt");
		Functions.createFile(s);
		try {
			System.out.println("Logging game to file...");
			PrintWriter writer = new PrintWriter(new FileOutputStream(s, true));
			writer.format("%s %s %3$tk:%3$tM:%3$tS %3$tD%n", name, players.toString(), new Date(timestamp));
			writer.close();
			System.out.println("Game logged");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String loadGames() {
		String s = String.format("%s/%s/%s", "app_data", guildId, "games.txt");
		if (new File(s).exists()) {
			try {
				Scanner reader = new Scanner(new FileInputStream(s));
				String games = "";

				while (reader.hasNextLine()) {
					games += reader.nextLine();
				}
				reader.close();

				return games;

			} catch (IOException ex) {
				ex.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
}
