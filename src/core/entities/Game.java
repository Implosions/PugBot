package core.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Database;
import core.entities.menus.RPSMenu;
import core.entities.menus.TeamPickerMenu;
import core.util.Trigger;
import core.util.Utils;
import net.dv8tion.jda.core.entities.User;

public class Game {
	private String guildId;
	private String name;
	private Long timestamp;
	private List<User> players;
	private User[] captains = new User[] { null, null };
	private RPSMenu rps = null;
	private TeamPickerMenu pickMenu = null;
	private Status status = Status.PICKING;

	public Game(String id, String name, List<User> players) {
		this.guildId = id;
		this.name = name;
		this.timestamp = System.currentTimeMillis();
		this.players = new ArrayList<User>(players);
		
		// Insert game into database
		Database.insertGame(timestamp, name, Long.valueOf(guildId));
		
		if (ServerManager.getServer(guildId).getSettings().randomizeCaptains()) {
			randomizeCaptains();
		}
		logGame();
	}
	
	public enum Status{
		PICKING,
		PLAYING;
	}

	public List<User> getPlayers() {
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
	
	public void sub(User target, User substitute){
		players.remove(target);
		players.add(substitute);
	}

	public boolean containsPlayer(String name) {
		for (User u : players) {
			if (u.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Chooses two random captains from the captainPool
	 */
	private void randomizeCaptains() {
		Random random = new Random();
		List<User> captainPool = getCaptainPool();
		captains[0] = captainPool.get(random.nextInt(captainPool.size()));
		while (captains[1] == null) {
			Integer i = random.nextInt(captainPool.size());
			if (!captainPool.get(i).equals(captains[0])) {
				captains[1] = captainPool.get(i);
			}
		}
		if(players.size() > 2){
			createRPSMenu();
		}
	}

	public User[] getCaptains() {
		return captains;
	}

	public String getName() {
		return name;
	}
	
	public Status getStatus(){
		return status;
	}
	
	private void setStatus(Status status){
		this.status = status;
	}
	
	public void createRPSMenu(){
		Trigger trigger = () -> createPickMenu();
		rps = new RPSMenu(captains[0], captains[1], trigger);
	}
	
	public void createPickMenu(){
		List<User> nonCaptainPlayers = new ArrayList<User>(players);
		nonCaptainPlayers.removeAll(Arrays.asList(captains));
		captains = rps.getResult();
		Trigger trigger = () -> pickingComplete();
		pickMenu = new TeamPickerMenu(captains, nonCaptainPlayers, trigger, ServerManager.getServer(guildId).getSettings().snakePick());
	}
	
	private void pickingComplete(){
		setStatus(Status.PLAYING);
		
		
		// Insert players in game into database
		for(User u : players){
			Database.insertPlayerGame(u.getIdLong(), timestamp, Long.valueOf(guildId));
		}
	}
	
	public void subCaptain(User sub, User target){
		for(Integer i = 0;i < 2;i++){
			if(captains[i] == target){
				captains[i] = sub;
			}
		}
		rps.complete();
		pickMenu.complete();
		createRPSMenu();
	}

	/*
	 * Adds eligible players to the captainPool
	 */
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
		Utils.createFile(s);
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
