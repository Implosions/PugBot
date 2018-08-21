package core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.Database;
import core.entities.menus.PUGPickMenuController;
import core.entities.menus.RPSMenuController;
import core.util.MatchMaker;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class Game {
	private Queue parentQueue;
	private long serverId;
	private long timestamp;
	private List<Member> players;
	private Member captain1;
	private Member captain2;
	private List<Channel> teamVoiceChannels;
	private GameStatus status = GameStatus.PICKING;
	private PUGPickMenuController pickController;
	private RPSMenuController rpsController;

	public Game(Queue queue, long serverId, List<Member> players) {
		this.parentQueue = queue;
		this.serverId = serverId;
		this.timestamp = System.currentTimeMillis();
		this.players = new ArrayList<Member>(players);
		
		// Insert game into database
		Database.insertGame(timestamp, queue.getId(), serverId);
		
		randomizeCaptains();
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
		if(target == captain1 || target == captain2){
			subCaptain(substitute, target);
		}
	}
	
	public boolean containsPlayer(Member player){
		return players.contains(player);
	}

	/**
	 * Chooses two random captains from the captainPool
	 * Creates the rps menu if there are more than 2 players
	 */
	private void randomizeCaptains() {
		Random random = new Random();
		List<Member> captainPool = getCaptainPool();
		
		captain1 = captainPool.get(random.nextInt(captainPool.size()));
		
		captain2 = new MatchMaker(serverId, captainPool).getMatch(captain1);
		
		startRPSGame();
	}
	
	public Member getCaptain1(){
		return captain1;
	}
	
	public Member getCaptain2(){
		return captain2;
	}

	/**
	 * @return the name of the queue that this game is from
	 */
	public String getQueueName() {
		return parentQueue.getName();
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
	
	public void startRPSGame(){
		new Thread(new Runnable(){
			public void run(){
				rpsController = new RPSMenuController(captain1, captain2);
				rpsController.start();
				
				Member winner = rpsController.getWinner();
				
				if(captain2 == winner){
					captain2 = captain1;
					captain1 = winner;
				}
				
				rpsController = null;
				startPUGPicking();
			}
		}).start();
	}
	
	public void startPUGPicking(){
		new Thread(new Runnable(){
			public void run(){
				List<Member> playerPool = new ArrayList<Member>(players);
				playerPool.remove(captain1);
				playerPool.remove(captain2);
				
				pickController = new PUGPickMenuController(captain1, captain2, playerPool, "1");
				pickController.start();
				
				pickingComplete();
			}
		}).start();
	}
	
	/**
	 * Inserts information into the database
	 */
	private void pickingComplete(){
		setStatus(GameStatus.PLAYING);		
		
		// Insert players in game into database
		for(Member m : players){
			Database.insertPlayerGame(m.getUser().getIdLong(), timestamp, serverId);
		}
		
		// Update captains
		if(captain1 != null){
			Database.updatePlayerGameCaptain(captain1.getUser().getIdLong(), timestamp, serverId, true);
		}
		
		if(captain2 != null){
			Database.updatePlayerGameCaptain(captain2.getUser().getIdLong(), timestamp, serverId, true);
		}
		
		// Add player pick order to database
		if(pickController != null){
			List<Member> picks = pickController.getPickedPlayers();
			for(int i = 0; i < picks.size();i++){
				Member player = picks.get(i);
				
				Database.updatePlayerGamePickOrder(player.getUser().getIdLong(), timestamp, serverId, i + 1);
			}
			
			// Post teams to pug channel
			String s = String.format("`Game: %s`", parentQueue.getName());
			
			ServerManager.getServer(serverId).getPugChannel()
				.sendMessage(Utils.createMessage(s, String.format("Teams:%n%s", pickController.getTeamsString()), true))
					.queue();
		}
		
		if(ServerManager.getServer(serverId).getSettingsManager().getCreateTeamVoiceChannels()){
			createVoiceChannels();
		}
		
		pickController = null;
	}
	
	private void createVoiceChannels() {
		teamVoiceChannels = new ArrayList<Channel>();
		
		createVoiceChannel(captain1);
		createVoiceChannel(captain2);
	}
	
	private void createVoiceChannel(Member member){
		try{
			Guild guild = ServerManager.getGuild(serverId);
			Category category = parentQueue.getSettingsManager().getVoiceChannelCategory();
			Channel channel = guild.getController().createVoiceChannel(member.getEffectiveName() + "'s team").complete();
				
			channel.getManager().setParent(category).queue();
			
			teamVoiceChannels.add(channel);
		}catch(Exception ex){
				System.out.println(ex.getMessage());
		}
	}
	
	private void deleteVoiceChannels(){
		if(teamVoiceChannels != null){
			try{
				for(Channel channel : teamVoiceChannels){
					channel.delete().queue();
				}
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public void finish(){
		cancelMenus();
		deleteVoiceChannels();
	}

	/**
	 * Removes all menus
	 */
	private void cancelMenus(){
		if(pickController != null){
			pickController.cancel();
		}
		
		if(rpsController != null){
			rpsController.cancel();
		}
	}
	
	/**
	 * Substitutes one non-captain player for a captain
	 * 
	 * @param sub the player replacing a captain
	 * @param target the captain to be replaced
	 */
	public void subCaptain(Member sub, Member target){
		if(captain1 == target){
			captain1 = sub;
		}else{
			captain2 = sub;
		}
		
		cancelMenus();
		startRPSGame();
	}

	/**
	 * Adds eligible players to the captainPool
	 * Returns players of not enough eligible players
	 */
	private List<Member> getCaptainPool() {
		List<Member> captainPool = new ArrayList<Member>();
		Integer minGames = parentQueue.getSettingsManager().getMinGamesPlayedToCaptain();
		
		for(Member m : players){
			if(Database.queryGetTotalGamesPlayed(m.getUser().getIdLong()) >= minGames){
				captainPool.add(m);
			}
		}
		
		if(captainPool.size() < 2){
			return players;
		}
		
		return captainPool;
	}
}
