package core.entities;

import java.awt.Color;
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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

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
		
		Database.insertGame(timestamp, queue.getId(), serverId);
		
		if(players.size() == 2){
			captain1 = players.get(0);
			captain2 = players.get(1);
			status = GameStatus.PLAYING;
			insertCaptains();
		}
		else{
			randomizeCaptains();
		}
		
		sendGameStartMessages();
	}
	
	public enum GameStatus{
		PICKING,
		PLAYING,
		FINISHED
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
	public long getTimestamp() {
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
		
		if(status == GameStatus.PICKING){
			if(target == captain1 || target == captain2){
				subCaptain(substitute, target);
			}else{
				if(pickController != null){
					pickController.cancel();
					startPUGPicking();
				}
			}
		}		
	}
	
	public boolean containsPlayer(Member player){
		return players.contains(player);
	}

	/**
	 * Chooses two random captains from the captainPool
	 */
	private void randomizeCaptains() {
		Random random = new Random();
		List<Member> captainPool = getCaptainPool();
		captain1 = captainPool.get(random.nextInt(captainPool.size()));
		captainPool.remove(captain1);
		captain2 = MatchMaker.getMatch(captain1, captainPool);
		
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
	
	public Queue getParentQueue(){
		return parentQueue;
	}
	
	/**
	 * @return the current status of this game
	 */
	public GameStatus getStatus(){
		return status;
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
				String pickingPattern = parentQueue.getSettingsManager().getPickPattern();
				
				playerPool.remove(captain1);
				playerPool.remove(captain2);
				
				pickController = new PUGPickMenuController(captain1, captain2, playerPool, pickingPattern);
				pickController.start();
				
				pickingComplete();
			}
		}).start();
	}
	
	/**
	 * Inserts information into the database
	 */
	private void pickingComplete(){
		status = GameStatus.PLAYING;		
		
		insertCaptains();
		insertPlayersInGame();
		createVoiceChannels();
		postTeamsToPUGChannel();
		
		pickController = null;
	}
	
	private void insertPlayersInGame(){
		List<Member> pickedPlayers = pickController.getPickedPlayers();
		
		for(int i = 0;i < pickedPlayers.size();i++){
			Member player = pickedPlayers.get(i);
			int team = pickController.getTeam(player);
			
			Database.insertPlayerGame(player.getUser().getIdLong(), timestamp, serverId, i+1, team);
		}
	}
	
	private void insertCaptains(){
		Database.insertPlayerGameCaptain(captain1.getUser().getIdLong(), timestamp, serverId, 1);
		Database.insertPlayerGameCaptain(captain2.getUser().getIdLong(), timestamp, serverId, 2);
	}
	
	private void postTeamsToPUGChannel(){
		TextChannel pugChannel = ServerManager.getServer(serverId).getPugChannel();
		Message message = Utils.createMessage(String.format("Game '%s' starting", getQueueName()),
											  String.format("Teams:%n%s", pickController.getTeamsString()),
											  true);
		
		pugChannel.sendMessage(message).queue();
	}
	
	private void createVoiceChannels() {
		if(teamVoiceChannels == null && ServerManager.getServer(serverId).getSettingsManager().getCreateTeamVoiceChannels()){
			teamVoiceChannels = new ArrayList<Channel>();
		
			createVoiceChannel(captain1);
			createVoiceChannel(captain2);
		}
	}
	
	private void createVoiceChannel(Member member){
		try{
			Guild guild = ServerManager.getGuild(serverId);
			Category category = parentQueue.getSettingsManager().getVoiceChannelCategory();
			Channel channel = guild.getController().createVoiceChannel("Team " + member.getEffectiveName()).complete();
				
			channel.getManager().setParent(category).queue();
			teamVoiceChannels.add(channel);
		}catch(Exception ex){
			//System.out.println(ex.getMessage());
			ex.printStackTrace();
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
	
	protected void finish(){
		cancelMenus();
		deleteVoiceChannels();
		
		status = GameStatus.FINISHED;
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
			return new ArrayList<Member>(players);
		}
		
		return captainPool;
	}
	
	private void sendGameStartMessages(){
		Message dm = Utils.createMessage("Game starting",
				  			String.format("Your game '%s' has begun", getQueueName()), true);
		TextChannel pugChannel = ServerManager.getServer(serverId).getPugChannel();
		StringBuilder builder = new StringBuilder();
		
		builder.append(String.format("**Captains: <@%s> & <@%s>**%n",
									 captain1.getUser().getId(),
									 captain2.getUser().getId()));
		
		for(Member m : players){
			try{
				PrivateChannel pc = m.getUser().openPrivateChannel().complete();
				
				pc.sendMessage(dm).queue();
			}catch(Exception ex){
				System.out.println("Error sending private message.\n" + ex.getMessage());
			}
			
			if(m == captain1 || m == captain2){
				continue;
			}
			
			builder.append(m.getEffectiveName() + ", ");
		}
		
		builder.delete(builder.length() - 2, builder.length());
		
		Message message = Utils.createMessage(String.format("Game '%s' has begun", getQueueName()),
											  builder.toString(), Color.blue);
		
		pugChannel.sendMessage(message).queue();
	}
	
	public boolean isCaptain(Member player){
		if(captain1 == player || captain2 == player){
			return true;
		}
		return false;
	}
	
	public void repick(){
		if(status == GameStatus.PLAYING){
			startPUGPicking();
			status = GameStatus.PICKING;
		}
	}
}
