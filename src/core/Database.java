package core;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.commands.CustomCommand;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.ServerManager;
import core.entities.settings.QueueSettingsManager;
import core.entities.settings.ServerSettingsManager;
import core.entities.settings.queuesettings.SettingMinGamesPlayedToCaptain;
import core.entities.settings.queuesettings.SettingPickPattern;
import core.entities.settings.queuesettings.SettingVoiceChannelCategory;
import core.entities.settings.serversettings.SettingAFKTimeout;
import core.entities.settings.serversettings.SettingCreateTeamVoiceChannels;
import core.entities.settings.serversettings.SettingDCTimeout;
import core.entities.settings.serversettings.SettingPUGChannel;
import core.entities.settings.serversettings.SettingQueueFinishTimer;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class Database {
	
	private static Connection conn = null;
	
	public Database(){
		try{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:app_data/pugbot.db");
			createTables();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void createTables(){
		try{
			Statement statement = conn.createStatement();
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "DiscordServer("
					+ "id INTEGER NOT NULL, "
					+ "name VARCHAR(50) NOT NULL, "
					+ "setting_AFKTimeout INTEGER NOT NULL DEFAULT 120, "
					+ "setting_DCTimeout INTEGER NOT NULL DEFAULT 5, "
					+ "setting_PUGChannel INTEGER NOT NULL DEFAULT 0, "
					+ "setting_QueueFinishTimer INTEGER NOT NULL DEFAULT 90, "
					+ "setting_createTeamVoiceChannels VARCHAR(5) DEFAULT 'true', "
					+ "PRIMARY KEY (id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "Player("
					+ "id INTEGER NOT NULL, "
					+ "name VARCHAR(50) NOT NULL, "
					+ "PRIMARY KEY (id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "Queue("
					+ "serverId INTEGER NOT NULL, "
					+ "id INTEGER NOT NULL, "
					+ "name VARCHAR(50) NOT NULL, "
					+ "maxPlayers INTEGER NOT NULL, "
					+ "active INTEGER NOT NULL DEFAULT 1, "
					+ "setting_MinGamesPlayedToCaptain INTEGER NOT NULL DEFAULT 15, "
					+ "setting_PickPattern VARCHAR(15) NOT NULL DEFAULT '1', "
					+ "setting_VoiceChannelCategory INTEGER NOT NULL DEFAULT 0, "
					+ "PRIMARY KEY (id, serverId), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "Game("
					+ "timestamp INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "serverId INTEGER NOT NULL, "
					+ "completion_timestamp INTEGER, "
					+ "winning_team INTEGER, "
					+ "PRIMARY KEY (timestamp, serverId), "
					+ "FOREIGN KEY (serverId) REFERENCES Queue(serverId)"
					+ "FOREIGN KEY (queueId) REFERENCES Queue(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "PlayerGame("
					+ "playerId INTEGER NOT NULL, "
					+ "timestamp INTEGER NOT NULL, "
					+ "serverId INTEGER NOT NULL, "
					+ "pickOrder INTEGER, "
					+ "captain INTEGER DEFAULT 0, "
					+ "team INTEGER, "
					+ "PRIMARY KEY (playerId, timestamp, serverId), "
					+ "FOREIGN KEY (playerId) REFERENCES Player(id), "
					+ "FOREIGN KEY (serverId) REFERENCES Game(serverId), "
					+ "FOREIGN KEY (timestamp) REFERENCES Game(timestamp)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "PlayerServer("
					+ "serverId INTEGER NOT NULL, "
					+ "playerId INTEGER NOT NULL, "
					+ "admin INTEGER NOT NULL, "
					+ "banned INTEGER NOT NULL, "
					+ "PRIMARY KEY (serverId, playerId), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id), "
					+ "FOREIGN KEY (playerId) REFERENCES Player(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "ServerCustomCommand("
					+ "serverId INTEGER NOT NULL, "
					+ "name VARCHAR(50) NOT NULL, "
					+ "message TEXT NOT NULL, "
					+ "PRIMARY KEY (serverId, name), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "PlayerInQueue("
					+ "serverId INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "playerId INTEGER NOT NULL, "
					+ "PRIMARY KEY (serverId, queueId, playerId), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id), "
					+ "FOREIGN KEY (queueId) REFERENCES Queue(id), "
					+ "FOREIGN KEY (playerId) REFERENCES Player(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "QueueNotification("
					+ "serverId INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "playerId INTEGER NOT NULL, "
					+ "playerCount INTEGER NOT NULL, "
					+ "PRIMARY KEY (serverId, queueId, playerId, playerCount), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id), "
					+ "FOREIGN KEY (queueId) REFERENCES Queue(id), "
					+ "FOREIGN KEY (playerId) REFERENCES Player(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "ServerRoleGroup("
					+ "serverId INTEGER NOT NULL, "
					+ "roleId INTEGER NOT NULL, "
					+ "PRIMARY KEY (serverId, roleId), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
					+ ")");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Inserts a new record into the DiscordServer table
	 * 
	 * @param id the id of the server
	 * @param name the name of the server
	 */
	public static void insertDiscordServer(Long id, String name){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO DiscordServer(id, name) VALUES(?, ?)");
			pStatement.setLong(1, id);
			pStatement.setString(2, name);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Insert a user into the Player table
	 * 
	 * @param id the id of the user
	 * @param name the name of the user
	 */
	public static void insertPlayer(Long id, String name){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO Player VALUES(?, ?)");
			pStatement.setLong(1, id);
			pStatement.setString(2, name);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Insert a queue into the Queue table
	 * 
	 * @param serverId the id of the server
	 * @param name the name of the queue
	 * @return The id of the queue created
	 */
	public static void insertQueue(long serverId, long queueId, String name, int maxPlayers){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					"INSERT OR IGNORE INTO Queue(serverId, id, name, maxPlayers) VALUES(?, ?, ?, ?)");
			
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			pStatement.setString(3, name);
			pStatement.setInt(4, maxPlayers);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Insert a game into the Game table 
	 * 
	 * @param timestamp the start time of the game
	 * @param queueName the name of the queue
	 * @param serverId the server id
	 */
	public static void insertGame(long timestamp, long queueId, long serverId){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					  "INSERT OR IGNORE INTO Game "
					+ "(timestamp, queueId, serverId)"
					+ "VALUES(?, ?, ?)");
			pStatement.setLong(1, timestamp);
			pStatement.setLong(2, queueId);
			pStatement.setLong(3, serverId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void updateGameInfo(long timestamp, long queueId, long serverId, long finishTime, int winningTeam){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					  "UPDATE Game "
					+ "SET completion_timestamp = ?, winning_team = ? "
					+ "WHERE timestamp = ? "
					+ "AND queueId = ? "
					+ "AND serverId = ?");
			
			pStatement.setLong(1, finishTime);
			pStatement.setInt(2, winningTeam);
			pStatement.setLong(3, timestamp);
			pStatement.setLong(4, queueId);
			pStatement.setLong(5, serverId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void insertPlayerGame(long playerId, long timestamp, long serverId, int pickOrder, int team){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					  "INSERT OR IGNORE INTO PlayerGame "
					+ "(playerId, timestamp, serverId, pickOrder, team) "
					+ "VALUES(?, ?, ?, ?, ?)");
			
			pStatement.setLong(1, playerId);
			pStatement.setLong(2, timestamp);
			pStatement.setLong(3, serverId);
			pStatement.setInt(4, pickOrder);
			pStatement.setInt(5, team);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void insertPlayerGameCaptain(long playerId, long timestamp, long serverId, int team){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					  "INSERT OR IGNORE INTO PlayerGame "
					+ "(playerId, timestamp, serverId, captain, team) "
					+ "VALUES(?, ?, ?, 1, ?)");
			
			pStatement.setLong(1, playerId);
			pStatement.setLong(2, timestamp);
			pStatement.setLong(3, serverId);
			pStatement.setInt(4, team);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param playerId the id of the player
	 * @return the number of games the player has participated in
	 */
	public static int queryGetTotalGamesPlayed(long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT count(playerId) FROM PlayerGame "
				+ "WHERE playerId = ?");
			
			pStatement.setLong(1, playerId);
			return pStatement.executeQuery().getInt(1);
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * @param serverId the discord server
	 * @param region the pug server region
	 * @return the ResultSet containing the pug server information
	 */
	public static ResultSet queryGetPugServers(Long serverId, String region){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT * FROM PugServer "
				+ "WHERE serverId = ? AND region = ?");
			
			pStatement.setLong(1, serverId);
			pStatement.setString(2, region);
			
			pStatement.setQueryTimeout(10);
			return pStatement.executeQuery();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param serverId the discord server
	 * @return the resultset containing all of the pug servers
	 */
	public static ResultSet queryGetPugServers(Long serverId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT * FROM PugServer "
				+ "WHERE serverId = ?");
			
			pStatement.setLong(1, serverId);
			
			pStatement.setQueryTimeout(10);
			return pStatement.executeQuery();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param serverId the discord server
	 * @param p1 the first player to compare
	 * @param p2 the second player to compare
	 * @return number representing the difference in pick order + the avg size of games played
	 */
	public static double queryGetPickOrderDiff(Long serverId, Long p1, Long p2){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT avg(p2.pickOrder - p1.pickOrder) "
					+ "+ (select avg(playerCount) FROM (SELECT count(timestamp) as playerCount from playergame where timestamp = p1.timestamp AND timestamp = p2.timestamp group by timestamp)) "
					+ "FROM (select * from playergame where playerid = ? AND serverId = ?) AS p1 "
					+ "JOIN (select * from playergame where playerid = ? AND serverId = ?) AS p2 "
					+ "ON p1.timestamp = p2.timestamp "
					+ "WHERE p1.captain = 0 AND p2.captain = 0 AND p1.pickorder > 0 AND p2.pickorder > 0");
			
			pStatement.setLong(1, p1);
			pStatement.setLong(3, p2);
			pStatement.setLong(2, serverId);
			pStatement.setLong(4, serverId);
			pStatement.setQueryTimeout(10);
			
			return pStatement.executeQuery().getDouble(1);
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return 0;
	}
	
	public static void insertPlayerServer(Long serverId, Long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO PlayerServer VALUES(?, ?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, playerId);
			pStatement.setInt(3, 0);
			pStatement.setInt(4, 0);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The discord server id
	 * @return List of user ids that have admin privileges
	 */
	public static Set<Long> queryGetAdminList(Long serverId){
		Set<Long> admins = new HashSet<Long>();
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerId FROM PlayerServer "
				+ "WHERE serverId = ? AND admin = 1");
			
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				admins.add(rs.getLong(1));
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return admins;
	}
	
	/**
	 * @param serverId The discord server id
	 * @return List of user ids that are currently banned from using the bot
	 */
	public static Set<Long> queryGetBanList(Long serverId){
		Set<Long> bans = new HashSet<Long>();
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerId FROM PlayerServer "
				+ "WHERE serverId = ? AND banned = 1");
			
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				bans.add(rs.getLong(1));
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return bans;
	}
	
	/**
	 * @param serverId the discord server
	 * @param playerId the player to update
	 * @param newStatus the new value to set, true = admin
	 */
	public static void updateAdminStatus(Long serverId, Long playerId, boolean newStatus){
		Integer value;
		
		if(newStatus){
			value = 1;
		}else{
			value = 0;
		}
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE PlayerServer SET admin = ? "
					+ "WHERE serverId = ? AND playerId = ?");
			pStatement.setInt(1, value);
			pStatement.setLong(2, serverId);
			pStatement.setLong(3, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId the discord server
	 * @param playerId the player to update
	 * @param newStatus the new value to set, true = banned
	 */
	public static void updateBanStatus(Long serverId, Long playerId, boolean newStatus){
		Integer value;
		
		if(newStatus){
			value = 1;
		}else{
			value = 0;
		}
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE PlayerServer SET banned = ? "
					+ "WHERE serverId = ? AND playerId = ?");
			pStatement.setInt(1, value);
			pStatement.setLong(2, serverId);
			pStatement.setLong(3, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @param name The name of the command
	 * @param message The output of the command
	 */
	public static void insertServerCustomCommand(long serverId, String name, String message){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT INTO ServerCustomCommand VALUES(?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setString(2, name);
			pStatement.setString(3, message);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return List of CustomCommand objects
	 */
	public static List<CustomCommand> getCustomCommands(Long serverId){
		List<CustomCommand> cmds = new ArrayList<CustomCommand>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT name, message FROM ServerCustomCommand WHERE serverId = ?");
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				String name = rs.getString(1);
				String message = rs.getString(2);
				
				cmds.add(new CustomCommand(name, message));
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return cmds;
	}
	
	/**
	 * @param serverId The id of the server
	 * @param name The name of the command to delete
	 */
	public static void deleteCustomCommand(Long serverId, String name){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM ServerCustomCommand WHERE serverId = ? AND name = ?");
			pStatement.setLong(1, serverId);
			pStatement.setString(2, name);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return A list of settings for the specified server
	 */
	public static void loadServerSettings(ServerSettingsManager manager){		
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT "
					+ "setting_AFKTimeout, "
					+ "setting_DCTimeout, "
					+ "setting_PUGChannel, "
					+ "setting_QueueFinishTimer, "
					+ "setting_createTeamVoiceChannels "
					+ "FROM DiscordServer WHERE id = ?");
			
			pStatement.setLong(1, manager.getServer().getId());
			
			ResultSet rs = pStatement.executeQuery();
			TextChannel channel = manager.getServer().getGuild().getTextChannelById(rs.getLong(3));
			
			manager.addSetting(new SettingAFKTimeout(rs.getInt(1)));
			manager.addSetting(new SettingDCTimeout(rs.getInt(2)));
			manager.addSetting(new SettingPUGChannel(channel));
			manager.addSetting(new SettingQueueFinishTimer(rs.getInt(4)));
			manager.addSetting(new SettingCreateTeamVoiceChannels(Boolean.valueOf(rs.getString(5))));
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @param id The id of the queue
	 * @return A list of settings for the specified queue
	 */
	public static void loadQueueSettings(QueueSettingsManager manager){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT "
					+ "setting_MinGamesPlayedToCaptain, "
					+ "setting_PickPattern, "
					+ "setting_VoiceChannelCategory "
					+ "FROM Queue WHERE serverId = ? AND id = ?");
			
			pStatement.setLong(1, manager.getServer().getId());
			pStatement.setLong(2, manager.getParentQueue().getId());
			
			ResultSet rs = pStatement.executeQuery();
			Category category = manager.getServer().getGuild().getCategoryById(rs.getLong(3));
			
			manager.addSetting(new SettingMinGamesPlayedToCaptain(rs.getInt(1)));
			manager.addSetting(new SettingPickPattern(rs.getString(2)));
			manager.addSetting(new SettingVoiceChannelCategory(category));
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Updates a specified server setting with a new value
	 * 
	 * @param serverId The id of the server
	 * @param setting The setting to change
	 * @param value The new value of the setting
	 */
	public static void updateServerSetting(long serverId, String setting, String value){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					String.format("UPDATE DiscordServer SET setting_%s = ? WHERE id = ?", setting));
			pStatement.setString(1, value);
			pStatement.setLong(2, serverId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Updates a specified queue setting with a new value
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param setting The setting to update
	 * @param value The new value of the setting
	 */
	public static void updateQueueSetting(long serverId, long queueId, String setting, String value){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					String.format("UPDATE Queue SET setting_%s = ? WHERE serverId = ? AND id = ?", setting));
			pStatement.setString(1, value);
			pStatement.setLong(2, serverId);
			pStatement.setLong(3, queueId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return A list of active queues in the specified server
	 */
	public static void loadServerQueues(QueueManager qm){
		long serverId = qm.getServerId();

		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT id, name, maxPlayers "
					+ "FROM Queue WHERE serverId = ? AND active = 1 ORDER BY id");
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				Queue q = new Queue(rs.getString(2), rs.getInt(3), rs.getLong(1), qm);
				
				for(Member player : getPlayersInQueue(serverId, q.getId())){
					q.addPlayerToQueueDirectly(player);
				}
				
				fillQueueNotifications(serverId, q.getId(), q);
				qm.addQueue(q);
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Inserts a record representing a player in a queue
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param playerId The id of the player
	 */
	public static void insertPlayerInQueue(long serverId, long queueId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO PlayerInQueue VALUES(?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			pStatement.setLong(3, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Delete a record of a player in queue
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param playerId The id of the player
	 */
	public static void deletePlayerInQueue(long serverId, long queueId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM PlayerInQueue "
					+ "WHERE serverId = ? AND queueId = ? AND playerId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			pStatement.setLong(3, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Delete all records of a group of players in queues
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 */
	public static void deletePlayersInQueueFromQueue(long serverId, long queueId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM PlayerInQueue "
					+ "WHERE serverId = ? AND playerId IN("
					+ "SELECT playerId FROM PlayerInQueue WHERE serverId = ? AND queueId = ?)");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, serverId);
			pStatement.setLong(3, queueId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Deletes a player from all queue records
	 * 	
	 * @param serverId The id of the server
	 * @param playerId The id of the player
	 */
	public static void deleteFromAllPlayerInQueue(long serverId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM PlayerInQueue "
					+ "WHERE serverId = ? AND playerId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @param queueId The id of the player
	 * @return A list of users that are in a specified queue
	 */
	public static List<Member> getPlayersInQueue(long serverId, long queueId){
		List<Member> playerList = new ArrayList<Member>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerId FROM PlayerInQueue "
					+ "WHERE serverId = ? AND queueId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			
			ResultSet rs = pStatement.executeQuery();
			Guild guild = ServerManager.getGuild(serverId);
			while(rs.next()){
				Member player = guild.getMemberById(rs.getLong(1));
				
				playerList.add(player);
			}
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return playerList;
	}
	
	/**
	 * Mark a queue as inactive, will no longer be loaded at startup
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 */
	public static void deactivateQueue(long serverId, long queueId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE Queue SET active = 0 "
					+ "WHERE serverId = ? AND id = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Insert a queue notification
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param playerId The id of the player
	 * @param playerCount The player count that the notification will trigger
	 */
	public static void insertQueueNotification(long serverId, long queueId, long playerId, int playerCount){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO QueueNotification VALUES(?, ?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			pStatement.setLong(3, playerId);
			pStatement.setInt(4, playerCount);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Delete queue notifications in a specific queue
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param playerId The id of the player
	 */
	public static void deleteQueueNotification(long serverId, long queueId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM QueueNotification "
					+ "WHERE serverId = ? AND queueId = ? AND playerId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			pStatement.setLong(3, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Delete all queue notifications for a specified player 
	 * 
	 * @param serverId The id of the server
	 * @param playerId The id of the player
	 */
	public static void deleteAllQueueNotification(long serverId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM QueueNotification "
					+ "WHERE serverId = ? AND playerId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, playerId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Adds all related notifications to a queue
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param queue The queue to add notifications to
	 */
	public static void fillQueueNotifications(long serverId, long queueId, Queue queue){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerCount, playerId FROM QueueNotification "
					+ "WHERE serverId = ? AND queueId = ? ORDER BY playerCount");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, queueId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				Member member = ServerManager.getGuild(String.valueOf(serverId)).getMemberById(rs.getLong(2));
				if(member != null){
					queue.addNotification(member, rs.getInt(1));
				}
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Updates a queue's name and max player count
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param name The name of the queue
	 * @param playerCount The maximum number of players
	 */
	public static void updateQueue(long serverId, long queueId, String name, int playerCount){
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE Queue SET name = ?, maxPlayers = ? "
					+ "WHERE serverId = ? AND id = ?");
			pStatement.setString(1, name);
			pStatement.setInt(2, playerCount);
			pStatement.setLong(3, serverId);
			pStatement.setLong(4, queueId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Inserts a new role group into the database
	 * 
	 * @param serverId The id of the server
	 * @param roleId The id of the role
	 */
	public static void insertGroup(long serverId, long roleId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO ServerRoleGroup VALUES(?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, roleId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Deletes a role group from a server
	 * 
	 * @param serverId The id of the server
	 * @param roleId The id of the role
	 */
	public static void deleteGroup(long serverId, long roleId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM ServerRoleGroup "
					+ "WHERE serverId = ? AND roleId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, roleId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the role groups for a server
	 * 
	 * @param serverId The id of the server
	 * @return HashMap<String, Role> containing the server's groups
	 */
	public static HashMap<String, Role> retrieveGroups(long serverId){
		HashMap<String, Role> dict = new HashMap<String, Role>();
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT roleId FROM ServerRoleGroup "
					+ "WHERE serverId = ?");
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				long roleId = rs.getLong(1);
				
				try{
					Role role = ServerManager.getGuild(String.valueOf(serverId)).getRoleById(roleId);
					
					dict.put(role.getName().toLowerCase(), role);
				}catch(Exception ex){
					System.out.println("Error retrieving role: " + roleId);
				}
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return dict;
	}
}
