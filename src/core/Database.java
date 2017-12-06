package core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
					+ "name VARCHAR(50) NOT NULL, "
					+ "serverId INTEGER NOT NULL, "
					+ "PRIMARY KEY (name, serverId), "
					+ "FOREIGN KEY (serverId) REFERENCES Server(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "Game("
					+ "timestamp INTEGER NOT NULL, "
					+ "queueName VARCHAR(50) NOT NULL, "
					+ "serverId INTEGER NOT NULL, "
					+ "PRIMARY KEY (timestamp, queueName, serverId), "
					+ "FOREIGN KEY (serverId) REFERENCES Queue(serverId)"
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
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO DiscordServer VALUES(?, ?)");
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
	 * Insert a queue into the Queue table
	 * 
	 * @param serverId the id of the server
	 * @param Name the name of the queue
	 */
	public static void insertQueue(Long serverId, String Name){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO Queue VALUES(?, ?)");
			pStatement.setString(1, Name);
			pStatement.setLong(2, serverId);
			
			pStatement.execute();
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
	public static void insertGame(Long timestamp, String queueName, Long serverId){
		try{
			PreparedStatement pStatement = conn.prepareStatement("INSERT OR IGNORE INTO Game VALUES(?, ?, ?)");
			pStatement.setLong(1, timestamp / 1000);
			pStatement.setString(2, queueName);
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
			pStatement.setQueryTimeout(10);
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
			pStatement.setQueryTimeout(10);
			
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
			pStatement.setQueryTimeout(10);
			
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
			pStatement.setLong(1, playerId);
			
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
			pStatement.setLong(1, playerId);
			
			pStatement.execute();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
}
