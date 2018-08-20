package core.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import core.Database;
import core.util.MatchMaker;
import core.util.Trigger;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;

public class Game {
	private Queue parent;
	private long serverId;
	private long timestamp;
	private List<Member> players;
	private Member[] captains = new Member[] { null, null };
	private Channel[] teamVoiceChannels = new Channel[2];
	private GameStatus status = GameStatus.PICKING;

	public Game(Queue parent, long serverId, List<Member> players) {
		this.parent = parent;
		this.serverId = serverId;
		this.timestamp = System.currentTimeMillis();
		this.players = new ArrayList<Member>(players);
		
		// Insert game into database
		Database.insertGame(timestamp, parent.getId(), serverId);
		
		if (parent.settings.randomizeCaptains()) {
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
	public List<Member> getPlayers() {
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
	public Member getPlayer(String name) {
		for (Member m : players) {
			if (m.getEffectiveName().equalsIgnoreCase(name)) {
				return m;
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
	public void sub(Member target, Member substitute){
		players.remove(target);
		players.add(substitute);
		
		// If the target is a captain and the picking has not started or has not finished yet call subCaptain
		// TODO check if picking is finished/in progress
		for(Member c : captains) {
			if (c == target) {
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
		for (Member m : players) {
			if (m.getEffectiveName().equalsIgnoreCase(name)) {
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
		List<Member> captainPool = getCaptainPool();
		
		captains[0] = captainPool.get(random.nextInt(captainPool.size()));
		
		captains[1] = new MatchMaker(serverId, captainPool).getMatch(captains[0]);
		
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
	public Member[] getCaptains() {
		return captains;
	}

	/**
	 * @return the name of the queue that this game is from
	 */
	public String getName() {
		return parent.getName();
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
	
	public void createRPSMenu(){
		
	}
	
	/**
	 * Creates a new TeamPickerMenu
	 */
	public void createPickMenu(){
		
	}
	
	/**
	 * Inserts information into the database
	 */
	private void pickingComplete(){
		setStatus(GameStatus.PLAYING);		
		
		// Insert players in game into database
		for(Member u : players){
			Database.insertPlayerGame(u.getIdLong(), timestamp, serverId);
		}
		
		// Update captains
		for(Member c : captains){
			if(c != null){
				Database.updatePlayerGameCaptain(c.getIdLong(), timestamp, serverId, true);
			}
		}
		
		// Add player pick order to database
		if(pickMenu != null){
			Integer count = 1;
			for (String id : pickMenu.getPickOrder()) {
				Database.updatePlayerGamePickOrder(Long.valueOf(id), timestamp, serverId, count);
				count++;
			}
			
			// Post teams to pug channel
			if(ServerManager.getServer(serverId).getSettings().postTeamsToPugChannel()){
				String s = String.format("`Game: %s`", parent.getName());
				ServerManager.getServer(serverId).getPugChannel()
				.sendMessage(Utils.createMessage(s, pickMenu.getPickedTeamsString(), true)).queue();
			}
		}
		
		if(parent.settings.randomizeCaptains() && 
				ServerManager.getServer(serverId).getSettings().createDiscordVoiceChannels()){
			createVoiceChannels();
		}
	}
	
	private void createVoiceChannels() {
		try{
			long catId = parent.settings.getVoiceChannelCategoryId();
			Category category = ServerManager.getGuild(String.valueOf(serverId)).getCategoryById(catId);
			
			for(int x = 0;x < captains.length;x++){	
				teamVoiceChannels[x] = ServerManager.getGuild(String.valueOf(serverId))
						.getController().createVoiceChannel(captains[x].getEffectiveName() + "'s team").complete();
				
				teamVoiceChannels[x].getManager().setParent(category).queue();
			}	
		}catch(Exception ex){
				System.out.println(ex.getMessage());
		}
		
	}
	
	private void deleteVoiceChannels(){
		if(teamVoiceChannels[0] != null && teamVoiceChannels[1] != null){
			try{
				teamVoiceChannels[0].delete().queue();
				teamVoiceChannels[1].delete().queue();
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public void finish(){
		removeMenus();
		deleteVoiceChannels();
	}

	/**
	 * Removes all menus
	 */
	private void removeMenus(){
		
	}
	
	/**
	 * Substitutes one non-captain player for a captain
	 * 
	 * @param sub the player replacing a captain
	 * @param target the captain to be replaced
	 */
	public void subCaptain(Member sub, Member target){
		new Thread(new Runnable(){
			public void run(){
				for(Integer i = 0;i < 2;i++){
					if(captains[i] == target){
						captains[i] = sub;
					}
				}
				removeMenus();
				createRPSMenu();
			}
		}).start();
	}

	/**
	 * Adds eligible players to the captainPool
	 * Returns players of not enough eligible players
	 */
	private List<Member> getCaptainPool() {
		List<Member> captainPool = new ArrayList<Member>();
		Integer minGames = parent.settings.getMinNumberOfGamesPlayedToCaptain();
		
		for(Member m : players){
			if(Database.queryGetTotalGamesPlayed(m.getUser().getIdLong()) >= minGames){
				captainPool.add(m);
			}
		}
		
		if(captainPool.size() > 1){
			return captainPool;
		}else{
			return players;
		}
	}
}
