package core.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import core.Database;
import core.entities.menus.RPSMenu;
import core.entities.menus.TeamPickerMenu;
import core.util.MatchMaker;
import core.util.Trigger;
import net.dv8tion.jda.core.entities.User;

public class Game {
	private String guildId;
	private String name;
	private Long timestamp;
	private List<User> players;
	private User[] captains = new User[] { null, null };
	private RPSMenu rps = null;
	private TeamPickerMenu pickMenu = null;
	private GameStatus status = GameStatus.PICKING;

	public Game(String id, String name, List<User> players) {
		this.guildId = id;
		this.name = name;
		this.timestamp = System.currentTimeMillis();
		this.players = new ArrayList<User>(players);
		
		// Insert game into database
		Database.insertGame(timestamp, name, Long.valueOf(guildId));
		
		if (ServerManager.getServer(guildId).getSettings().randomizeCaptains()) {
			randomizeCaptains();
		}else{
			pickingComplete();
		}
	}
	
	public enum GameStatus{
		PICKING,
		PLAYING;
	}

	/**
	 * @return the list of players in this game
	 */
	public List<User> getPlayers() {
		return players;
	}

	/**
	 * @return the start time of this game in milliseconds
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Checks if a player matching a specific name exists in this game
	 * 
	 * @param name the name of the player
	 * @return the player matching the name
	 */
	public User getPlayer(String name) {
		for (User u : players) {
			if (u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Substitutes one player in game with one out of game
	 * 
	 * @param target the player to replace
	 * @param substitute the player that will replace the target
	 */
	public void sub(User target, User substitute){
		players.remove(target);
		players.add(substitute);
		for(User c : captains){
			if(c == target){
				subCaptain(substitute, target);
			}
		}
	}

	/**
	 * Checks if a player by the specified name is in this game
	 * 
	 * @param name the name of the player
	 * @return true if the player is in this game
	 */
	public boolean containsPlayer(String name) {
		for (User u : players) {
			if (u.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Chooses two random captains from the captainPool
	 * Creates the rps menu if there are more than 2 players
	 */
	private void randomizeCaptains() {
		Random random = new Random();
		List<User> captainPool = getCaptainPool();
		
		captains[0] = captainPool.get(random.nextInt(captainPool.size()));
		
		captains[1] = new MatchMaker(guildId, players).getMatch(captains[0]);
		
		if(players.size() > 2){
			// Create RPS menu on a new thread
			new Thread(new Runnable(){
				public void run(){
					createRPSMenu();
				}
			}).start();
		}else{
			pickingComplete();
		}
	}

	/**
	 * @return array of captains
	 */
	public User[] getCaptains() {
		return captains;
	}

	/**
	 * @return the name of the queue that this game is from
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the current status of this game
	 */
	public GameStatus getStatus(){
		return status;
	}
	
	/**
	 * Sets the game's status
	 * 
	 * @param status the status to change to
	 */
	private void setStatus(GameStatus status){
		this.status = status;
	}
	
	/**
	 * Creates a new RPSMenu
	 */
	public void createRPSMenu(){
		Trigger trigger = () -> createPickMenu();
		rps = new RPSMenu(captains[0], captains[1], trigger);
	}
	
	/**
	 * Creates a new TeamPickerMenu
	 */
	public void createPickMenu(){
		List<User> nonCaptainPlayers = new ArrayList<User>(players);
		nonCaptainPlayers.removeAll(Arrays.asList(captains));
		captains = rps.getResult();
		Trigger trigger = () -> pickingComplete();
		pickMenu = new TeamPickerMenu(captains, nonCaptainPlayers, trigger, ServerManager.getServer(guildId).getSettings().snakePick());
	}
	
	/**
	 * Inserts information into the database
	 */
	private void pickingComplete(){
		setStatus(GameStatus.PLAYING);
		
		
		// Insert players in game into database
		for(User u : players){
			Database.insertPlayerGame(u.getIdLong(), timestamp, Long.valueOf(guildId));
		}
		
		// Update captains
		for(User c : captains){
			if(c != null){
				Database.updatePlayerGameCaptain(c.getIdLong(), timestamp, Long.valueOf(guildId), true);
			}
		}
		
		// Add player pick order
		if(pickMenu != null){
			Integer count = 1;
			for (String id : pickMenu.getPickOrder()) {
				Database.updatePlayerGamePickOrder(Long.valueOf(id), timestamp, Long.valueOf(guildId), count);
				count++;
			}
		}
	}
	
	/**
	 * Removes all menus
	 */
	public void removeMenus(){
		if(rps != null && !rps.finished()){
			rps.complete();
		}
		if(pickMenu != null && !pickMenu.finished()){
			pickMenu.complete();
		}
	}
	
	/**
	 * Substitutes one non-captain player for a captain
	 * 
	 * @param sub the player replacing a captain
	 * @param target the captain to be replaced
	 */
	public void subCaptain(User sub, User target){
		for(Integer i = 0;i < 2;i++){
			if(captains[i] == target){
				captains[i] = sub;
			}
		}
		removeMenus();
		createRPSMenu();
	}

	/**
	 * Adds eligible players to the captainPool
	 * Returns players of not enough eligible players
	 */
	private List<User> getCaptainPool() {
		List<User> captainPool = new ArrayList<User>();
		Integer minGames = ServerManager.getServer(guildId).getSettings().minNumberOfGames();
		
		for(User p : players){
			if(Database.queryGetTotalGamesPlayed(p.getIdLong()) >= minGames){
				captainPool.add(p);
			}
		}
		
		if(captainPool.size() > 1){
			return captainPool;
		}else{
			return players;
		}
	}
}
