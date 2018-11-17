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
	private List<Channel> teamVoiceChannels;
	private PUGTeam team1 = new PUGTeam();
	private PUGTeam team2 = new PUGTeam();
	
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
			team1.setCaptain(players.get(0));
			team2.setCaptain(players.get(1));
			
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
		
		if(target == team1.getCaptain() || target == team2.getCaptain()){
			subCaptain(substitute, target);
		}
		else{
			if(status == GameStatus.PLAYING){
				if(team1.getPlayers().contains(target)){
					team1.removePlayer(target);
					team1.addPlayer(substitute);
				}
				else{
					team2.removePlayer(target);
					team2.addPlayer(substitute);
				}
			}
			
			if(status == GameStatus.PICKING){
				cancelMenus();
				startPUGPicking();
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
		Member captain1 = captainPool.get(random.nextInt(captainPool.size()));
		
		captainPool.remove(captain1);
		
		Member captain2 = MatchMaker.getMatch(captain1, captainPool);
		
		team1.setCaptain(captain1);
		team2.setCaptain(captain2);
		
		startRPSGame();
	}
	
	public PUGTeam getTeam1(){
		return team1;
	}
	
	public PUGTeam getTeam2(){
		return team2;
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
	
	private void startRPSGame(){
		new Thread(new Runnable(){
			public void run(){
				RPSMenuController localRpsController = new RPSMenuController(team1.getCaptain(), team2.getCaptain());
				rpsController = localRpsController;
				localRpsController.start();
				
				if(localRpsController.isCancelled()){
					return;
				}
				
				Member winner = localRpsController.getWinner();
				
				if(team2.getCaptain() == winner){
					team2.setCaptain(team1.getCaptain());
					team1.setCaptain(winner);
				}
				
				rpsController = null;
				startPUGPicking();
			}
		}).start();
	}
	
	private void startPUGPicking(){
		new Thread(new Runnable(){
			public void run(){
				List<Member> playerPool = new ArrayList<Member>(players);
				String pickingPattern = parentQueue.getSettingsManager().getPickPattern();
				
				playerPool.remove(team1.getCaptain());
				playerPool.remove(team2.getCaptain());
				
				PUGPickMenuController localPickController = 
						new PUGPickMenuController(team1.getCaptain(), team2.getCaptain(), playerPool, pickingPattern);
				pickController = localPickController;
				localPickController.start();
				
				if(localPickController.isCancelled()){
					return;
				}
				
				pickingComplete();
			}
		}).start();
	}
	
	/**
	 * Inserts information into the database
	 */
	private void pickingComplete(){
		status = GameStatus.PLAYING;
		team1 = pickController.getTeam1();
		team2 = pickController.getTeam2();
		
		insertCaptains();
		insertPlayersInGame();
		createVoiceChannels();
		postTeamsToPUGChannel();
	}
	
	private void insertPlayersInGame(){
		List<Member> pickedPlayers = pickController.getPickedPlayers();
		
		for(int i = 0;i < pickedPlayers.size();i++){
			Member player = pickedPlayers.get(i);
			int team = (getTeam(player) == team1) ? 1 : 2;
			
			Database.insertPlayerGame(player.getUser().getIdLong(), timestamp, serverId, i+1, team);
		}
	}
	
	private void insertCaptains(){
		long c1 = team1.getCaptain().getUser().getIdLong();
		long c2 = team2.getCaptain().getUser().getIdLong();
		
		Database.insertPlayerGameCaptain(c1, timestamp, serverId, 1);
		Database.insertPlayerGameCaptain(c2, timestamp, serverId, 2);
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
		
			createVoiceChannel(team1.getCaptain());
			createVoiceChannel(team2.getCaptain());
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
			pickController = null;
		}
		
		if(rpsController != null){
			rpsController.cancel();
			rpsController = null;
		}
	}
	
	/**
	 * Substitutes one non-captain player for a captain
	 * 
	 * @param sub the player replacing a captain
	 * @param target the captain to be replaced
	 */
	public void subCaptain(Member sub, Member target){
		if(team1.getCaptain() == target){
			team1.setCaptain(sub);
		}else{
			team2.setCaptain(sub);
		}
		
		if(status == GameStatus.PICKING){
			cancelMenus();
			startRPSGame();
		}
	}

	/**
	 * Adds eligible players to the captainPool
	 * Returns players of not enough eligible players
	 */
	private List<Member> getCaptainPool() {
		List<Member> captainPool = new ArrayList<Member>();
		int minGames = parentQueue.getSettingsManager().getMinGamesPlayedToCaptain();
		
		for(Member m : players){
			int gamesPlayed = Database.queryGetPlayerTotalCompletedGames(
					serverId, m.getUser().getIdLong(), parentQueue.getId());
			
			if(gamesPlayed >= minGames){
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
		
		long c1 = team1.getCaptain().getUser().getIdLong();
		long c2 = team2.getCaptain().getUser().getIdLong();
		
		builder.append(String.format("**Captains: <@%d> & <@%d>**%n", c1, c2));
		
		for(Member m : players){
			if(players.size() > 2 && isCaptain(m)){
				continue;
			}
			
			try{
				PrivateChannel pc = m.getUser().openPrivateChannel().complete();
				
				pc.sendMessage(dm).queue();
			}catch(Exception ex){
				System.out.println("Error sending private message.\n" + ex.getMessage());
			}
			
			
			
			builder.append(m.getEffectiveName() + ", ");
		}
		
		builder.delete(builder.length() - 2, builder.length());
		
		Message message = Utils.createMessage(String.format("Game '%s' has begun", getQueueName()),
											  builder.toString(), Color.blue);
		
		pugChannel.sendMessage(message).queue();
	}
	
	public boolean isCaptain(Member player){
		return team1.getCaptain() == player || team2.getCaptain() == player;
	}
	
	public void repick(){
		if(status == GameStatus.PLAYING){
			cancelMenus();
			startPUGPicking();
			status = GameStatus.PICKING;
		}
	}
	
	public void setStatus(GameStatus newStatus){
		status = newStatus;
	}
	
	public PUGTeam getTeam(Member player){
		if(player == team1.getCaptain() || team1.getPlayers().contains(player)){
			return team1;
		}
		
		return team2;
	}
	
	public void swapPlayers(Member p1, Member p2){
		PUGTeam p1Team = getTeam(p1);
		PUGTeam p2Team = getTeam(p2);
		
		p1Team.removePlayer(p1);
		p1Team.addPlayer(p2);
		p2Team.removePlayer(p2);
		p2Team.addPlayer(p1);
		
		Database.swapPlayers(timestamp, p1.getUser().getIdLong(), p2.getUser().getIdLong());
	}
}
