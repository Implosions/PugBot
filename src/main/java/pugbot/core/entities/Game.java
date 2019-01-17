package pugbot.core.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import pugbot.Utils;
import pugbot.core.Database;
import pugbot.core.entities.menus.ConfirmationMenu;
import pugbot.core.entities.menus.MapPickMenuController;
import pugbot.core.entities.menus.PUGPickMenuController;
import pugbot.core.entities.menus.RPSMenuController;
import pugbot.core.entities.settings.QueueSettingsManager;

public class Game {
	private Queue parentQueue;
	
	private long serverId;
	private long timestamp;
	
	private List<Member> players;
	private PUGTeam[] teams = new PUGTeam[2];
	private GameStatus status = GameStatus.PICKING;
	private PUGPickMenuController pickController;
	private RPSMenuController rpsController;
	private MapPickMenuController mapPickController;
	private ConfirmationMenu startingOrderConfirmationMenu;

	public Game(Queue queue, long serverId, List<Member> players) {
		this.parentQueue = queue;
		this.serverId = serverId;
		this.timestamp = System.currentTimeMillis();
		
		Database.insertGame(timestamp, queue.getId(), serverId);
		
		if(players.size() == 2) {
			teams[0] = new PUGTeam(players.get(0));
			teams[1] = new PUGTeam(players.get(1));
			
			status = GameStatus.PLAYING;
			insertCaptains();
		}
		else {
			int minGamesSetting = parentQueue.getSettingsManager().getMinGamesPlayedToCaptain();
			MatchMaker mm = new MatchMaker(players, serverId, parentQueue.getId(), minGamesSetting);
			Member[] captains = mm.getRandomizedCaptains();
			this.players = mm.getOrderedPlayerList();
			
			teams[0] = new PUGTeam(captains[0]);
			teams[1] = new PUGTeam(captains[1]);
			
			startRPSGame();
		}
		
		sendGameStartMessages();
	}
	
	public enum GameStatus {
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
		
		if(target == teams[0].getCaptain() || target == teams[1].getCaptain()){
			subCaptain(substitute, target);
		}
		else{
			if(status == GameStatus.PLAYING){
				if(teams[0].getPlayers().contains(target)){
					teams[0].removePlayer(target);
					teams[0].addPlayer(substitute, null);
				}
				else{
					teams[1].removePlayer(target);
					teams[1].addPlayer(substitute, null);
				}
			}
			
			if(status == GameStatus.PICKING && pickController != null){
				cancelMenus();
				startPUGPicking();
			}
		}
	}
	
	public boolean containsPlayer(Member player){
		return players.contains(player);
	}
	
	public PUGTeam[] getPUGTeams(){
		return teams;
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
				RPSMenuController localRpsController = new RPSMenuController(teams[0].getCaptain(), teams[1].getCaptain());
				rpsController = localRpsController;
				localRpsController.start();
				
				if(localRpsController.isCancelled()){
					return;
				}
				
				Member winner = localRpsController.getWinner();
				
				if(teams[1].getCaptain() == winner) {
					teams[1].setCaptain(teams[0].getCaptain());
					teams[0].setCaptain(winner);
				}
				
				startConfirmationMenu();
			}
		}).start();
	}
	
	private void startConfirmationMenu() {
		new Thread(new Runnable() {
			public void run() {
				ConfirmationMenu localcm = new ConfirmationMenu(teams[0].getCaptain(), "Take first pick?");
				startingOrderConfirmationMenu = localcm;
				localcm.start();
				
				if(localcm.isCancelled()){
					return;
				}
				
				if(!localcm.getResult()) {
					Member tmp = teams[0].getCaptain();
					
					teams[0].setCaptain(teams[1].getCaptain());
					teams[1].setCaptain(tmp);
				}
				
				startPUGPicking();
			}
		}).start();
	}
	
	private void startPUGPicking(){
		new Thread(new Runnable(){
			public void run(){
				List<Member> playerPool = new ArrayList<Member>(players);
				String pickingPattern = parentQueue.getSettingsManager().getPickPattern();
				
				playerPool.remove(teams[0].getCaptain());
				playerPool.remove(teams[1].getCaptain());
				
				PUGPickMenuController localPickController = 
						new PUGPickMenuController(teams[0].getCaptain(), teams[1].getCaptain(), playerPool, pickingPattern);
				pickController = localPickController;
				
				localPickController.start();
				
				if(localPickController.isCancelled()){
					return;
				}
				
				startMapPicking();
			}
		}).start();
	}
	
	private void startMapPicking() {
		QueueSettingsManager settings = getParentQueue().getSettingsManager();
		int mapCount = settings.getMapCount();
		int poolSize = settings.getMapPool().size();
		
		if(mapCount == 0 || poolSize < 2 || (mapCount >= poolSize)) {
			pickingComplete();
			return;
		}
		
		new Thread(new Runnable(){
			public void run(){
				List<String> mapPool = new ArrayList<>(settings.getMapPool());
				
				MapPickMenuController localMapPickController = 
						new MapPickMenuController(teams[0].getCaptain(), teams[1].getCaptain(), 
								mapCount, mapPool, settings.getPickStyle());
				mapPickController = localMapPickController;
				localMapPickController.start();
				
				if(localMapPickController.isCancelled()){
					return;
				}
				
				pickingComplete();
			}
		}).start();
	}
	
	private void pickingComplete(){
		status = GameStatus.PLAYING;
		teams = pickController.getTeams();
		
		createVoiceChannels();
		postTeamsToPUGChannel();
	}
	
	private void insertPlayersInGame() {
		for(int i = 0; i < teams.length; i++) {
			for(Member player : teams[i].getPlayers()) {
				Integer pickOrder = teams[i].getPickOrder(player);
				
				Database.insertPlayerGame(player.getUser().getIdLong(), timestamp, serverId, pickOrder, i + 1);
			}
		}
	}
	
	private void insertCaptains(){
		long c1 = teams[0].getCaptain().getUser().getIdLong();
		long c2 = teams[1].getCaptain().getUser().getIdLong();
		
		Database.insertPlayerGameCaptain(c1, timestamp, serverId, 1);
		Database.insertPlayerGameCaptain(c2, timestamp, serverId, 2);
	}
	
	private void postTeamsToPUGChannel(){
		String output = String.format("**Teams:**%n%s", pickController.getTeamsString());
		
		if(mapPickController != null) {
			String maps = String.join(", ", mapPickController.getPickedMaps());
			
			output += String.format("%n%n**Maps:** %s", maps);
		}
				
				
		TextChannel pugChannel = ServerManager.getServer(serverId).getPugChannel();
		Message message = Utils.createMessage(String.format("Game '%s' teams picked", getQueueName()),
											  output, true);
		
		for(Member m : players) {
			if(!isCaptain(m)) {
				try {
					m.getUser().openPrivateChannel().complete().sendMessage(message).queue();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		pugChannel.sendMessage(message).queue();
	}
	
	private void createVoiceChannels() {
		boolean createChannels = ServerManager.getServer(serverId).getSettingsManager().getCreateTeamVoiceChannels();
		
		if(createChannels){
			Category category = parentQueue.getSettingsManager().getVoiceChannelCategory();
			
			teams[0].createVoiceChannel(category);
			teams[1].createVoiceChannel(category);
		}
	}
	
	private void deleteVoiceChannels(){
		teams[0].deleteVoiceChannel();
		teams[1].deleteVoiceChannel();
	}
	
	protected void finish(){
		insertCaptains();
		insertPlayersInGame();
		insertMaps();
		
		status = GameStatus.FINISHED;
		rpsController = null;
		pickController = null;
		mapPickController = null;
	}
	
	protected void cleanup() {
		cancelMenus();
		deleteVoiceChannels();
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
		
		if(mapPickController != null) {
			mapPickController.cancel();
			mapPickController = null;
		}
		
		if(startingOrderConfirmationMenu != null) {
			startingOrderConfirmationMenu.cancel();
			startingOrderConfirmationMenu = null;
		}
	}
	
	/**
	 * Substitutes one non-captain player for a captain
	 * 
	 * @param sub the player replacing a captain
	 * @param target the captain to be replaced
	 */
	public void subCaptain(Member sub, Member target) {
		if(teams[0].getCaptain() == target){
			teams[0].setCaptain(sub);
			teams[0].updateVoiceChannel();
		}else{
			teams[1].setCaptain(sub);
			teams[1].updateVoiceChannel();
		}
		
		if(status == GameStatus.PICKING) {
			cancelMenus();
			startRPSGame();
		}
	}
	
	private void sendGameStartMessages() {
		Message dm = Utils.createMessage("Game starting",
				  			String.format("Your game '%s' has begun", getQueueName()), true);
		TextChannel pugChannel = ServerManager.getServer(serverId).getPugChannel();
		StringBuilder builder = new StringBuilder();
		
		long c1 = teams[0].getCaptain().getUser().getIdLong();
		long c2 = teams[1].getCaptain().getUser().getIdLong();
		
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
		return teams[0].getCaptain() == player || teams[1].getCaptain() == player;
	}
	
	public void repick(){
		cancelMenus();
		startConfirmationMenu();
		status = GameStatus.PICKING;
	}
	
	public PUGTeam getTeam(Member player){
		if(player == teams[0].getCaptain() || teams[0].getPlayers().contains(player)){
			return teams[0];
		}
		
		return teams[1];
	}
	
	public void swapPlayers(Member p1, Member p2){
		PUGTeam p1Team = getTeam(p1);
		PUGTeam p2Team = getTeam(p2);
		int p1PickOrder = p1Team.getPickOrder(p1);
		int p2PickOrder = p2Team.getPickOrder(p2);
		
		p1Team.removePlayer(p1);
		p1Team.addPlayer(p2, p2PickOrder);
		p2Team.removePlayer(p2);
		p2Team.addPlayer(p1, p1PickOrder);
	}
	
	private void insertMaps() {
		if(mapPickController != null) {
			for(String map : mapPickController.getPickedMaps()) {
				Database.insertGameMap(serverId, getParentQueue().getId(), timestamp, map);
			}
		}
	}
}
