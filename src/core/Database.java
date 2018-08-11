package core;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.commands.CustomCommand;
import core.entities.Queue;
import core.entities.ServerManager;
import core.entities.Setting;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class Database {
	
	private static Connection conn = null;
	
	public Database(){
		try{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:app_data/bullybot.db");
			createTables();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * Creates database tables if they do not already exist
	 */
	private void createTables(){
		try{
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "DiscordServer("
					+ "id INTEGER NOT NULL, "
					+ "name VARCHAR(50) NOT NULL, "
					+ "setting_AFKTimeout INTEGER NOT NULL DEFAULT 120, "
					+ "setting_DCTimeout INTEGER NOT NULL DEFAULT 5, "
					+ "setting_PUGChannel VARCHAR(50) NOT NULL DEFAULT 'pugs', "
					+ "setting_QueueFinishTimer INTEGER NOT NULL DEFAULT 90, "
					+ "setting_postPickedTeamsToPugChannel VARCHAR(5) DEFAULT 'true', "
					+ "setting_createDiscordVoiceChannels VARCHAR(5) DEFAULT 'true', "
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
					+ "setting_minNumberOfGamesToCaptain INTEGER NOT NULL DEFAULT 15, "
					+ "setting_randomizeCaptains VARCHAR(5) NOT NULL DEFAULT 'true', "
					+ "setting_snakePick VARCHAR(5) NOT NULL DEFAULT 'false', "
					+ "setting_voiceChannelCategoryId INTEGER NOT NULL DEFAULT 0, "
					+ "PRIMARY KEY (id, serverId), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "Game("
					+ "timestamp INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "serverId INTEGER NOT NULL, "
					+ "PRIMARY KEY (timestamp, queueId, serverId), "
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
					+ "PRIMARY KEY (playerId, timestamp, serverId), "
					+ "FOREIGN KEY (playerId) REFERENCES Player(id), "
					+ "FOREIGN KEY (serverId) REFERENCES Game(serverId), "
					+ "FOREIGN KEY (timestamp) REFERENCES Game(timestamp)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "PugServer("
					+ "serverId INTEGER NOT NULL, "
					+ "ip VARCHAR(20) NOT NULL, "
					+ "port INTEGER, "
					+ "name VARCHAR(30), "
					+ "password VARCHAR(30), "
					+ "region VARCHAR(10), "
					+ "gameId INTEGER NOT NULL, "
					+ "PRIMARY KEY (ip, serverId), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
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
			
			pStatement.execute();
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
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * The count of queues stored for a server
	 * 
	 * @param serverId The id of the server
	 * @return The number of queues saved for a server
	 */
	public static int getQueueCount(long serverId){
		int count = 0;
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT count(id) FROM Queue WHERE serverId = ?");
			pStatement.setLong(1, serverId);
			
			
			ResultSet rs = pStatement.executeQuery();
			count = rs.getInt(1);
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return count;
	}
	
	/**
	 * Insert a queue into the Queue table
	 * 
	 * @param serverId the id of the server
	 * @param name the name of the queue
	 * @return The id of the queue created
	 */
	public static int insertQueue(long serverId, String name, int maxPlayers){
		int id = getQueueCount(serverId);
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO Queue(serverId, id, name, maxPlayers) VALUES(?, ?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, id);
			pStatement.setString(3, name);
			pStatement.setInt(4, maxPlayers);
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return id;
	}
	
	/**
	 * Insert a game into the Game table 
	 * 
	 * @param timestamp the start time of the game
	 * @param queueName the name of the queue
	 * @param serverId the server id
	 */
	public static void insertGame(long timestamp, int queueId, long serverId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO Game VALUES(?, ?, ?)");
			pStatement.setLong(1, timestamp / 1000);
			pStatement.setInt(2, queueId);
			pStatement.setLong(3, serverId);
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Inserts a new record into the PlayerGame table
	 * 
	 * @param playerId the id of the player
	 * @param timestamp the time of the game start
	 * @param serverId the id of the server
	 */
	public static void insertPlayerGame(Long playerId, Long timestamp, Long serverId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO PlayerGame (playerId, timestamp, serverId) VALUES(?, ?, ?)");
			pStatement.setLong(1, playerId);
			pStatement.setLong(2, timestamp / 1000);
			pStatement.setLong(3, serverId);
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Updates a PlayerGame record with a new pickOrder
	 * 
	 * @param playerId the id of the player
	 * @param timestamp the time of the game start
	 * @param serverId the id of the server
	 * @param pickOrder the pick order of the player
	 */
	public static void updatePlayerGamePickOrder(Long playerId, Long timestamp, Long serverId, Integer pickOrder){
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE PlayerGame SET pickOrder = ? "
					+ "WHERE playerId = ? AND timestamp = ? AND serverId = ?");
			pStatement.setInt(1, pickOrder);
			pStatement.setLong(2, playerId);
			pStatement.setLong(3, timestamp / 1000);
			pStatement.setLong(4, serverId);
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Updates a record in PlayerGame with captain information
	 * 
	 * @param playerId the id of the player
	 * @param timestamp the time of the game start
	 * @param serverId the id of the server
	 * @param captain true if the player is a captain
	 */
	public static void updatePlayerGameCaptain(Long playerId, Long timestamp, Long serverId, boolean captain){
		Integer captainInt = 0;
		if(captain){
			captainInt = 1;
		}
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE PlayerGame SET captain = ? "
					+ "WHERE playerId = ? AND timestamp = ? AND serverId = ?");
			
			pStatement.setInt(1, captainInt);
			pStatement.setLong(2, playerId);
			pStatement.setLong(3, timestamp / 1000);
			pStatement.setLong(4, serverId);
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param playerId the id of the player
	 * @return the number of games the player has participated in
	 */
	public static Integer queryGetTotalGamesPlayed(Long playerId){
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
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId the discord server
	 * @return List of user ids that have admin privileges
	 */
	public static List<String> queryGetAdminList(Long serverId){
		List<String> admins = new ArrayList<String>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerId FROM PlayerServer "
				+ "WHERE serverId = ? AND admin = 1");
			
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				admins.add(rs.getString(1));
			}
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return admins;
	}
	
	/**
	 * @param serverId the discord server
	 * @return List of user ids that are currently banned from using the bot
	 */
	public static List<String> queryGetBanList(Long serverId){
		List<String> bans = new ArrayList<String>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerId FROM PlayerServer "
				+ "WHERE serverId = ? AND banned = 1");
			
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				bans.add(rs.getString(1));
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
			
			pStatement.execute();
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
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @param name The name of the command
	 * @param message The output of the command
	 */
	public static void insertServerCustomCommand(Long serverId, String name, String message){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT INTO ServerCustomCommand VALUES(?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setString(2, name);
			pStatement.setString(3, message);
			
			pStatement.execute();
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
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return A list of settings for the specified server
	 */
	public static List<Setting> getServerSettings(long serverId){
		List<Setting> settings = new ArrayList<Setting>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT "
					+ "setting_AFKTimeout, "
					+ "setting_DCTimeout, "
					+ "setting_PUGChannel, "
					+ "setting_QueueFinishTimer, "
					+ "setting_postPickedTeamsToPugChannel, "
					+ "setting_createDiscordVoiceChannels "
					+ "FROM DiscordServer WHERE id = ?");
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();

			settings.add(new Setting("AFKTimeout", rs.getInt(1),
					"minutes", "The amount of time before a user is removed from all queues if no input is detected"));
			settings.add(new Setting("DCTimeout", rs.getInt(2),
					"minutes", "The amount of time before a user is removed from all queues after a disconnect is detected"));
			settings.add(new Setting("PUGChannel", rs.getString(3),
					null, "The channel that PUG related input and output will be focused in"));
			settings.add(new Setting("queueFinishTimer", rs.getInt(4),
					"seconds", "The amount of time for allowing users to re-add to queues after finishing a game"));
			settings.add(new Setting("postPickedTeamsToPugChannel", Boolean.valueOf(rs.getString(5)),
					null, "Post the picked teams to the PUG channel"));
			settings.add(new Setting("createDiscordVoiceChannels", Boolean.valueOf(rs.getString(6)),
					null, "Create discord voice channels for teams on game start"));
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return settings;
	}
	
	/**
	 * @param serverId The id of the server
	 * @param id The id of the queue
	 * @return A list of settings for the specified queue
	 */
	public static List<Setting> getQueueSettings(long serverId, int id){
		List<Setting> settings = new ArrayList<Setting>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT setting_minNumberOfGamesToCaptain, "
					+ "setting_randomizeCaptains, "
					+ "setting_snakePick, "
					+ "setting_voiceChannelCategoryId "
					+ "FROM Queue WHERE serverId = ? AND id = ?");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, id);
			
			ResultSet rs = pStatement.executeQuery();
			
			settings.add(new Setting("minNumberOfGamesToCaptain", rs.getInt(1),
					null, "The minimum number of games played for a player to be able to captain"));
			settings.add(new Setting("randomizeCaptains", Boolean.valueOf(rs.getString(2)),
					null, "Enables the bot to randomly select captains and allow picking through discord"));
			settings.add(new Setting("snakePick", Boolean.valueOf(rs.getString(3)),
					null, "Enables a snake at the end of picking"));
			settings.add(new Setting("voiceChannelCategoryId", rs.getLong(4),
					null, "The ID of the channel category to put generated voice channels in"));
			
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return settings;
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
	public static void updateQueueSetting(long serverId, int queueId, String setting, String value){
		try{
			PreparedStatement pStatement = conn.prepareStatement(
					String.format("UPDATE Queue SET setting_%s = ? WHERE serverId = ? AND id = ?", setting));
			pStatement.setString(1, value);
			pStatement.setLong(2, serverId);
			pStatement.setInt(3, queueId);
			
			pStatement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return A list of active queues in the specified server
	 */
	public static List<Queue> getServerQueueList(long serverId){
		List<Queue> queueList = new ArrayList<Queue>();
		
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT id, name, maxPlayers "
					+ "FROM Queue WHERE serverId = ? AND active = 1 ORDER BY id");
			pStatement.setLong(1, serverId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				Queue q = new Queue(rs.getString(2), rs.getInt(3), serverId, rs.getInt(1));
				
				for(User p : getPlayersInQueue(serverId, q.getId())){
					q.addPlayerToQueueDirectly(p);
				}
				fillQueueNotifications(serverId, q.getId(), q);
				queueList.add(q);
			}
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return queueList;
	}
	
	/**
	 * Inserts a record representing a player in a queue
	 * 
	 * @param serverId The id of the server
	 * @param queueId The id of the queue
	 * @param playerId The id of the player
	 */
	public static void insertPlayerInQueue(long serverId, int queueId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO PlayerInQueue VALUES(?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
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
	public static void deletePlayerInQueue(long serverId, int queueId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM PlayerInQueue "
					+ "WHERE serverId = ? AND queueId = ? AND playerId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
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
	public static void deletePlayersInQueueFromQueue(long serverId, int queueId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM PlayerInQueue "
					+ "WHERE serverId = ? AND playerId IN("
					+ "SELECT playerId FROM PlayerInQueue WHERE serverId = ? AND queueId = ?)");
			pStatement.setLong(1, serverId);
			pStatement.setLong(2, serverId);
			pStatement.setInt(3, queueId);
			
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
	public static List<User> getPlayersInQueue(long serverId, int queueId){
		List<User> playerList = new ArrayList<User>();
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerId FROM PlayerInQueue "
					+ "WHERE serverId = ? AND queueId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
			
			ResultSet rs = pStatement.executeQuery();
			while(rs.next()){
				User player = ServerManager.getGuild(String.valueOf(serverId)).getMemberById(rs.getLong(1)).getUser();
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
	public static void deactivateQueue(long serverId, int queueId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE Queue SET active = 0 "
					+ "WHERE serverId = ? AND id = ?");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
			
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
	public static void insertQueueNotification(long serverId, int queueId, long playerId, int playerCount){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO QueueNotification VALUES(?, ?, ?, ?)");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
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
	public static void deleteQueueNotification(long serverId, int queueId, long playerId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("DELETE FROM QueueNotification "
					+ "WHERE serverId = ? AND queueId = ? AND playerId = ?");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
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
	public static void fillQueueNotifications(long serverId, int queueId, Queue queue){
		try{
			PreparedStatement pStatement = conn.prepareStatement("SELECT playerCount, playerId FROM QueueNotification "
					+ "WHERE serverId = ? AND queueId = ? ORDER BY playerCount");
			pStatement.setLong(1, serverId);
			pStatement.setInt(2, queueId);
			
			ResultSet rs = pStatement.executeQuery();
			
			while(rs.next()){
				Member member = ServerManager.getGuild(String.valueOf(serverId)).getMemberById(rs.getLong(2));
				if(member != null){
					queue.addNotification(member.getUser(), rs.getInt(1));
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
	public static void updateQueue(long serverId, int queueId, String name, int playerCount){
		try{
			PreparedStatement pStatement = conn.prepareStatement("UPDATE Queue SET name = ?, maxPlayers = ? "
					+ "WHERE serverId = ? AND id = ?");
			pStatement.setString(1, name);
			pStatement.setInt(2, playerCount);
			pStatement.setLong(3, serverId);
			pStatement.setInt(4, queueId);
			
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
