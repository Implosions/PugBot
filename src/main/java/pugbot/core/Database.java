package pugbot.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import pugbot.core.commands.CustomCommand;
import pugbot.core.entities.CommandManager;
import pugbot.core.entities.Queue;
import pugbot.core.entities.QueueManager;
import pugbot.core.entities.Server;
import pugbot.core.entities.ServerManager;
import pugbot.core.entities.settings.QueueSettingsManager;
import pugbot.core.entities.settings.ServerSettingsManager;
import pugbot.core.entities.settings.queuesettings.SettingMapCount;
import pugbot.core.entities.settings.queuesettings.SettingMapPickingStyle;
import pugbot.core.entities.settings.queuesettings.SettingMapPool;
import pugbot.core.entities.settings.queuesettings.SettingMinGamesPlayedToCaptain;
import pugbot.core.entities.settings.queuesettings.SettingPickPattern;
import pugbot.core.entities.settings.queuesettings.SettingRoleRestrictions;
import pugbot.core.entities.settings.queuesettings.SettingVoiceChannelCategory;
import pugbot.core.entities.settings.serversettings.SettingAFKTimeout;
import pugbot.core.entities.settings.serversettings.SettingCreateTeamVoiceChannels;
import pugbot.core.entities.settings.serversettings.SettingDCTimeout;
import pugbot.core.entities.settings.serversettings.SettingPUGChannel;
import pugbot.core.entities.settings.serversettings.SettingQueueFinishTimer;

public class Database {
	
	private static Connection _conn = null;
	
	public static void createConnection() {
		try{
			Class.forName("org.sqlite.JDBC");
			_conn = DriverManager.getConnection("jdbc:sqlite:app_data/pugbot.db");
			createTables();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private static void createTables() {
		try(Statement statement = _conn.createStatement()) {
			
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
					+ "setting_MapCount INTEGER NOT NULL DEFAULT 0, "
					+ "setting_MapPickingStyle INTEGER NOT NULL DEFAULT 0, "
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
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "RPSGame("
					+ "timestamp INTEGER NOT NULL, "
					+ "playerId INTEGER NOT NULL, "
					+ "result INTEGER, "
					+ "PRIMARY KEY (timestamp, playerId), "
					+ "FOREIGN KEY (playerId) REFERENCES Player(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "QueueRole("
					+ "serverId INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "roleId INTEGER NOT NULL, "
					+ "PRIMARY KEY (serverId, queueId, roleId), "
					+ "FOREIGN KEY (queueId) REFERENCES Queue(id), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "QueueMap("
					+ "serverId INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "name VARCHAR(20) NOT NULL, "
					+ "PRIMARY KEY (serverId, queueId, name), "
					+ "FOREIGN KEY (queueId) REFERENCES Queue(id), "
					+ "FOREIGN KEY (serverId) REFERENCES DiscordServer(id)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "GameMap("
					+ "serverId INTEGER NOT NULL, "
					+ "queueId INTEGER NOT NULL, "
					+ "timestamp INTEGER NOT NULL, "
					+ "mapName VARCHAR(20) NOT NULL, "
					+ "PRIMARY KEY (serverId, queueId, timestamp, mapName), "
					+ "FOREIGN KEY (queueId) REFERENCES QueueMap(queueId), "
					+ "FOREIGN KEY (serverId) REFERENCES QueueMap(serverId), "
					+ "FOREIGN KEY (mapName) REFERENCES QueueMap(name), "
					+ "FOREIGN KEY (timestamp) REFERENCES Game(timestamp)"
					+ ")");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ "ServerDisabledCommand("
					+ "serverId INTEGER NOT NULL, "
					+ "commandName VARCHAR(20) NOT NULL, "
					+ "PRIMARY KEY (serverId, commandName), "
					+ "FOREIGN KEY (serverId) REFERENCES Server(id)"
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
	public static void insertDiscordServer(long id, String name){
		String sql = "INSERT OR IGNORE INTO DiscordServer(id, name) VALUES(?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)) {
			statement.setLong(1, id);
			statement.setString(2, name);
			
			statement.executeUpdate();
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
	public static void insertPlayer(long id, String name){
		String sql = "INSERT OR REPLACE INTO Player VALUES(?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)) {
			statement.setLong(1, id);
			statement.setString(2, name);
			
			statement.executeUpdate();
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
	public static void insertQueue(long serverId, long queueId, String name, int maxPlayers) {
		String sql = "INSERT OR IGNORE INTO Queue(serverId, id, name, maxPlayers) VALUES(?, ?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setString(3, name);
			statement.setInt(4, maxPlayers);
			
			statement.executeUpdate();
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
	public static void insertGame(long timestamp, long queueId, long serverId) {
		String sql = "INSERT OR IGNORE INTO Game "
				   + "(timestamp, queueId, serverId)"
				   + "VALUES(?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, timestamp);
			statement.setLong(2, queueId);
			statement.setLong(3, serverId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void updateGameInfo(long timestamp, long queueId, long serverId, long finishTime, int winningTeam) {
		String sql = "UPDATE Game "
				   + "SET completion_timestamp = ?, winning_team = ? "
				   + "WHERE timestamp = ? "
				   + "AND queueId = ? "
				   + "AND serverId = ?";
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, finishTime);
			statement.setInt(2, winningTeam);
			statement.setLong(3, timestamp);
			statement.setLong(4, queueId);
			statement.setLong(5, serverId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void insertPlayerGame(long playerId, long timestamp, long serverId, Integer pickOrder, int team) {
		String sql = "INSERT OR REPLACE INTO PlayerGame "
				   + "(playerId, timestamp, serverId, pickOrder, team, captain) "
				   + "VALUES(?, ?, ?, ?, ?, 0)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)) {
			
			
			statement.setLong(1, playerId);
			statement.setLong(2, timestamp);
			statement.setLong(3, serverId);
			statement.setObject(4, pickOrder, Types.INTEGER);
			statement.setInt(5, team);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void insertPlayerGameCaptain(long playerId, long timestamp, long serverId, int team) {
		String sql = "INSERT OR REPLACE INTO PlayerGame "
				+ "(playerId, timestamp, serverId, captain, team, pickOrder) "
				+ "VALUES(?, ?, ?, 1, ?, 0)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, playerId);
			statement.setLong(2, timestamp);
			statement.setLong(3, serverId);
			statement.setInt(4, team);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId the discord server id
	 * @param p1 the id of first player to compare
	 * @param p2 the id of second player to compare
	 * @return the difference in pick order between p1 and p2
	 */
	public static double queryGetPickOrderDiff(long serverId, long p1, long p2){
		String sql = "SELECT avg(p2.pickOrder - p1.pickOrder) "
				+ "FROM (select pickOrder, timestamp from playergame where playerid = ? AND serverId = ? AND captain = 0) AS p1 "
				+ "JOIN (select pickOrder, timestamp from playergame where playerid = ? AND serverId = ? AND captain = 0) AS p2 "
				+ "ON p1.timestamp = p2.timestamp "
				+ "ORDER BY p1.timestamp DESC "
				+ "LIMIT 10";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, p1);
			statement.setLong(3, p2);
			statement.setLong(2, serverId);
			statement.setLong(4, serverId);
			
			return statement.executeQuery().getDouble(1);
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return 0;
	}
	
	public static void insertPlayerServer(long serverId, long playerId){
		String sql = "INSERT OR IGNORE INTO PlayerServer VALUES(?, ?, 0, 0)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The discord server id
	 * @return List of user ids that have admin privileges
	 */
	public static List<Long> queryGetAdminList(long serverId){
		String sql = "SELECT playerId FROM PlayerServer "
				+ "WHERE serverId = ? AND admin = 1";
		List<Long> admins = new ArrayList<Long>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			
			try (ResultSet rs = statement.executeQuery();){
				while(rs.next()){
					admins.add(rs.getLong(1));
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return admins;
	}
	
	/**
	 * @param serverId The discord server id
	 * @return List of user ids that are currently banned from using the bot
	 */
	public static List<Long> queryGetBanList(long serverId){
		String sql = "SELECT playerId FROM PlayerServer "
				+ "WHERE serverId = ? AND banned = 1";
		List<Long> bans = new ArrayList<Long>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					bans.add(rs.getLong(1));
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return bans;
	}
	
	/**
	 * @param serverId the discord server
	 * @param playerId the player to update
	 * @param isAdmin the new value to set
	 */
	public static void updateAdminStatus(long serverId, long playerId, boolean isAdmin){
		String sql = "UPDATE PlayerServer SET admin = ? "
				+ "WHERE serverId = ? AND playerId = ?";
		int value;
		
		if(isAdmin){
			value = 1;
		}else{
			value = 0;
		}
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setInt(1, value);
			statement.setLong(2, serverId);
			statement.setLong(3, playerId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId the discord server
	 * @param playerId the player to update
	 * @param isBanned the new value to set, true = banned
	 */
	public static void updateBanStatus(long serverId, long playerId, boolean isBanned){
		String sql = "UPDATE PlayerServer SET banned = ? "
				+ "WHERE serverId = ? AND playerId = ?";
		int value;
		
		if(isBanned){
			value = 1;
		}else{
			value = 0;
		}
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setInt(1, value);
			statement.setLong(2, serverId);
			statement.setLong(3, playerId);
			
			statement.executeUpdate();
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
		String sql = "INSERT INTO ServerCustomCommand VALUES(?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setString(2, name);
			statement.setString(3, message);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return List of CustomCommand objects
	 */
	public static void loadCustomCommands(CommandManager manager, Server server) {
		String sql = "SELECT name, message FROM ServerCustomCommand WHERE serverId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, server.getId());
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					String name = rs.getString(1);
					String message = rs.getString(2);
					
					manager.addCommand(new CustomCommand(server, name, message));
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}

	}
	
	/**
	 * @param serverId The id of the server
	 * @param name The name of the command to delete
	 */
	public static void deleteCustomCommand(long serverId, String name){
		String sql = "DELETE FROM ServerCustomCommand WHERE serverId = ? AND name = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setString(2, name);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param serverId The id of the server
	 * @return A list of settings for the specified server
	 */
	public static void loadServerSettings(ServerSettingsManager manager){	
		String sql = "SELECT "
				+ "setting_AFKTimeout, "
				+ "setting_DCTimeout, "
				+ "setting_PUGChannel, "
				+ "setting_QueueFinishTimer, "
				+ "setting_createTeamVoiceChannels "
				+ "FROM DiscordServer WHERE id = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, manager.getServer().getId());
			
			try(ResultSet rs = statement.executeQuery()){
				TextChannel channel = manager.getServer().getGuild().getTextChannelById(rs.getLong(3));
				long guildId = manager.getServer().getGuild().getIdLong();
				
				manager.addSetting(new SettingAFKTimeout(guildId, rs.getInt(1)));
				manager.addSetting(new SettingDCTimeout(guildId, rs.getInt(2)));
				manager.addSetting(new SettingPUGChannel(guildId, channel));
				manager.addSetting(new SettingQueueFinishTimer(guildId, rs.getInt(4)));
				manager.addSetting(new SettingCreateTeamVoiceChannels(guildId, Boolean.valueOf(rs.getString(5))));
			}
			
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
		String sql = "SELECT "
				+ "setting_MinGamesPlayedToCaptain, "
				+ "setting_PickPattern, "
				+ "setting_VoiceChannelCategory, "
				+ "setting_MapCount, "
				+ "setting_MapPickingStyle "
				+ "FROM Queue WHERE serverId = ? AND id = ?";
		
		Guild guild = manager.getServer().getGuild();
		long queueId = manager.getParentQueue().getId();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, guild.getIdLong());
			statement.setLong(2, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				Category category = guild.getCategoryById(rs.getLong(3));
				
				manager.addSetting(new SettingMinGamesPlayedToCaptain(guild.getIdLong(), queueId, rs.getInt(1)));
				manager.addSetting(new SettingPickPattern(guild.getIdLong(), queueId, rs.getString(2)));
				manager.addSetting(new SettingVoiceChannelCategory(guild.getIdLong(), queueId, category));
				manager.addSetting(new SettingMapCount(guild.getIdLong(), queueId, rs.getInt(4)));
				manager.addSetting(new SettingMapPickingStyle(guild.getIdLong(), queueId, rs.getInt(5)));
			}

		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		List<Role> roles = getQueueRoles(guild, queueId);
		List<String> maps = getQueueMaps(guild.getIdLong(), queueId);
		
		manager.addSetting(new SettingRoleRestrictions(guild.getIdLong(), queueId, roles));
		manager.addSetting(new SettingMapPool(guild.getIdLong(), queueId, maps));
	}
	
	private static List<String> getQueueMaps(long serverId, long queueId) {
		String sql = "SELECT name FROM QueueMap WHERE serverId = ? AND queueId = ?";
		List<String> maps = new ArrayList<>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					String map = rs.getString(1);
					
					maps.add(map);
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return maps;
	}
	
	private static List<Role> getQueueRoles(Guild guild, long queueId){
		String sql = "SELECT roleId FROM QueueRole WHERE serverId = ? AND queueId = ?";
		List<Role> roles = new ArrayList<>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, guild.getIdLong());
			statement.setLong(2, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					Role role = guild.getRoleById(rs.getLong(1));
					
					if(role != null){
						roles.add(role);
					}
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return roles;
	}
	
	/**
	 * Updates a specified server setting with a new value
	 * 
	 * @param serverId The id of the server
	 * @param setting The setting to change
	 * @param value The new value of the setting
	 */
	public static void updateServerSetting(long serverId, String setting, String value){
		String sql = String.format("UPDATE DiscordServer SET setting_%s = ? WHERE id = ?", setting);
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setString(1, value);
			statement.setLong(2, serverId);
			
			statement.executeUpdate();
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
		String sql = String.format("UPDATE Queue SET setting_%s = ? WHERE serverId = ? AND id = ?", setting);
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setString(1, value);
			statement.setLong(2, serverId);
			statement.setLong(3, queueId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void deleteQueueRole(long serverId, long queueId, long roleId){
		String sql = "DELETE FROM QueueRole WHERE serverId = ? AND QueueId = ? AND RoleId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, roleId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void addQueueRole(long serverId, long queueId, long roleId){
		String sql = "INSERT OR IGNORE INTO QueueRole VALUES(?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, roleId);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	
	
	/**
	 * @param serverId The id of the server
	 * @return A list of active queues in the specified server
	 */
	public static void loadServerQueues(QueueManager qm){
		String sql = "SELECT id, name, maxPlayers "
				+ "FROM Queue WHERE serverId = ? AND active = 1 ORDER BY id";
		long serverId = qm.getServerId();

		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					Queue q = new Queue(rs.getString(2), rs.getInt(3), rs.getLong(1), qm);
					
					for(Member player : getPlayersInQueue(serverId, q.getId())){
						q.addPlayerToQueueDirectly(player);
					}
					
					fillQueueNotifications(serverId, q.getId(), q);
					qm.addQueue(q);
				}
			}
			
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
		String sql = "INSERT OR IGNORE INTO PlayerInQueue VALUES(?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, playerId);
			
			statement.executeUpdate();
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
		String sql = "DELETE FROM PlayerInQueue "
				+ "WHERE serverId = ? AND queueId = ? AND playerId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, playerId);
			
			statement.executeUpdate();
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
		String sql = "DELETE FROM PlayerInQueue "
				+ "WHERE serverId = ? AND playerId IN("
				+ "SELECT playerId FROM PlayerInQueue WHERE serverId = ? AND queueId = ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, serverId);
			statement.setLong(3, queueId);
			
			statement.executeUpdate();
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
		String sql = "DELETE FROM PlayerInQueue "
				+ "WHERE serverId = ? AND playerId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			
			statement.executeUpdate();
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
		String sql = "SELECT playerId FROM PlayerInQueue "
				+ "WHERE serverId = ? AND queueId = ?";
		List<Member> playerList = new ArrayList<Member>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				Guild guild = ServerManager.getGuild(serverId);
				
				while(rs.next()){
					Member player = guild.getMemberById(rs.getLong(1));
					
					playerList.add(player);
				}
			}
			
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
		String sql = "UPDATE Queue SET active = 0 "
				+ "WHERE serverId = ? AND id = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			
			statement.executeUpdate();
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
		String sql = "INSERT OR IGNORE INTO QueueNotification VALUES(?, ?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, playerId);
			statement.setInt(4, playerCount);
			
			statement.executeUpdate();
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
	public static void deleteQueueNotification(long serverId, long queueId, long playerId) {
		String sql = "DELETE FROM QueueNotification "
				+ "WHERE serverId = ? AND queueId = ? AND playerId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, playerId);
			
			statement.executeUpdate();
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
		String sql = "DELETE FROM QueueNotification "
				+ "WHERE serverId = ? AND playerId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			
			statement.executeUpdate();
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
		String sql = "SELECT playerCount, playerId FROM QueueNotification "
				+ "WHERE serverId = ? AND queueId = ? ORDER BY playerCount";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					Member member = ServerManager.getGuild(String.valueOf(serverId)).getMemberById(rs.getLong(2));
					if(member != null){
						queue.addNotification(member, rs.getInt(1));
					}
				}
			}
			
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
		String sql = "UPDATE Queue SET name = ?, maxPlayers = ? "
				+ "WHERE serverId = ? AND id = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setString(1, name);
			statement.setInt(2, playerCount);
			statement.setLong(3, serverId);
			statement.setLong(4, queueId);
			
			statement.executeUpdate();
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
		String sql = "INSERT OR IGNORE INTO ServerRoleGroup VALUES(?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, roleId);
			
			statement.executeUpdate();
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
		String sql = "DELETE FROM ServerRoleGroup "
				+ "WHERE serverId = ? AND roleId = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, roleId);
			
			statement.executeUpdate();
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
		String sql = "SELECT roleId FROM ServerRoleGroup "
				+ "WHERE serverId = ?";
		
		HashMap<String, Role> dict = new HashMap<>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					long roleId = rs.getLong(1);
					
					try{
						Role role = ServerManager.getGuild(String.valueOf(serverId)).getRoleById(roleId);
						
						dict.put(role.getName().toLowerCase(), role);
					}catch(Exception ex){
						System.out.println("Error retrieving role: " + roleId);
					}
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return dict;
	}
	
	public static void insertRPSGame(long timestamp, long playerId, int result){
		String sql = "INSERT OR IGNORE INTO RPSGame "
				+ "(timestamp, playerId, result)"
				+ "VALUES(?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, timestamp);
			statement.setLong(2, playerId);
			statement.setInt(3, result);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static int queryGetServerTotalGames(long serverId, long timeframe){
		String sql = "SELECT count(timestamp) "
				+ "FROM Game WHERE serverId = ? "
				+ "AND timestamp >= ? "
				+ "AND completion_timestamp > 0";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, timeframe);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static int queryGetServerUniquePlayerCount(long serverId, long timeframe){
		String sql = "SELECT count(DISTINCT playerId) "
				+ "FROM PlayerGame WHERE serverId = ? "
				+ "AND timestamp >= ?";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, timeframe);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static int queryGetPlayerTotalCompletedGames(long serverId, long playerId, long queueId){
		String sql = "SELECT count(playerId) "
				+ "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				+ "WHERE Game.serverId = ? "
				+ "AND playerId = ? "
				+ "AND queueId = ? "
				+ "AND completion_timestamp > 0";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			statement.setLong(3, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static int queryGetPlayerTotalWins(long serverId, long playerId, long queueId){
		String sql = "SELECT count(winning_team) "
				+ "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				+ "WHERE Game.serverId = ? "
				+ "AND playerId = ? "
				+ "AND queueId = ? "
				+ "AND completion_timestamp > 0 "
				+ "AND winning_team = team";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			statement.setLong(3, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static int queryGetPlayerTotalLosses(long serverId, long playerId, long queueId){
		String sql = "SELECT count(team) "
				+ "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				+ "WHERE Game.serverId = ? "
				+ "AND playerId = ? "
				+ "AND queueId = ? "
				+ "AND completion_timestamp > 0 "
				+ "AND NOT winning_team = team "
				+ "AND NOT winning_team = 0";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			statement.setLong(3, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static int queryGetPlayerAvgPickPosition(long serverId, long playerId, long queueId){
		String sql = "SELECT (sum(pickOrder) / count(pickOrder)) "
				+ "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				+ "WHERE Game.serverId = ? "
				+ "AND playerId = ? "
				+ "AND queueId = ? "
				+ "AND NOT captain = 1";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			statement.setLong(3, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static int queryGetPlayerCaptainWinPercent(long serverId, long playerId, long queueId){
		String sql = "SELECT ((count(playerId) * 100) / "
				  + "(SELECT count(playerId) "
				  + "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				  + "WHERE Game.serverId = ? AND playerId = ? AND queueId = ? AND completion_timestamp > 0 "
				  + "AND captain = 1)) "
				+ "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				+ "WHERE Game.serverId = ? "
				+ "AND playerId = ? "
				+ "AND queueId = ? "
				+ "AND captain = 1 "
				+ "AND winning_team = team "
				+ "AND completion_timestamp > 0";
		int result = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			statement.setLong(3, queueId);
			statement.setLong(4, serverId);
			statement.setLong(5, playerId);
			statement.setLong(6, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				result = rs.getInt(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public static Date queryGetPlayerLastPlayedDate(long serverId, long playerId, long queueId){
		String sql = "SELECT Game.timestamp "
				+ "FROM PlayerGame JOIN Game ON PlayerGame.timestamp = Game.timestamp "
				+ "WHERE Game.serverId = ? "
				+ "AND playerId = ? "
				+ "AND queueId = ? "
				+ "ORDER BY Game.timestamp DESC "
				+ "LIMIT 1";
		
		long timestamp = 0;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, playerId);
			statement.setLong(3, queueId);
			
			try(ResultSet rs = statement.executeQuery()){
				timestamp = rs.getLong(1);
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return new Date(timestamp);
	}
	
	public static void swapPlayers(long timestamp, long p1, long p2){
		String sql = "UPDATE PlayerGame SET team = "
				+ "(SELECT SUM(team) FROM PlayerGame WHERE timestamp = ? AND (playerId = ? OR playerId = ?)) - team "
				+ "WHERE timestamp = ? AND (playerId = ? or playerId = ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, timestamp);
			statement.setLong(2, p1);
			statement.setLong(3, p2);
			statement.setLong(4, timestamp);
			statement.setLong(5, p1);
			statement.setLong(6, p2);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void addQueueMap(long serverId, long queueId, String mapName){
		String sql = "INSERT OR IGNORE INTO QueueMap VALUES(?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setString(3, mapName);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void deleteQueueMap(long serverId, long queueId, String mapName){
		String sql = "DELETE FROM QueueMap WHERE "
				+ "serverId = ? AND "
				+ "queueId = ? AND "
				+ "name = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setString(3, mapName);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void insertGameMap(long serverId, long queueId, long timestamp, String mapName) {
		String sql = "INSERT OR REPLACE INTO GameMap "
				   + "(serverId, queueId, timestamp, mapName) "
				   + "VALUES(?, ?, ?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)) {
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, timestamp);
			statement.setString(4, mapName);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static List<String> getDisabledCommands(long serverId) {
		String sql = "SELECT commandName FROM ServerDisabledCommand WHERE serverId = ?";
		List<String> commands = new ArrayList<>();
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			
			try(ResultSet rs = statement.executeQuery()){
				while(rs.next()){
					String cmd = rs.getString(1);
					
					commands.add(cmd);
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return commands;
	}
	
	public static void insertDisabledCommand(long serverId, String cmdName) {
		String sql = "INSERT OR IGNORE INTO ServerDisabledCommand "
				   + "VALUES(?, ?)";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)) {
			statement.setLong(1, serverId);
			statement.setString(2, cmdName);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void deleteDisabledCommand(long serverId, String cmdName) {
		String sql = "DELETE FROM ServerDisabledCommand "
				+ "WHERE serverId = ? AND commandName = ?";
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)) {
			statement.setLong(1, serverId);
			statement.setString(2, cmdName);
			
			statement.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static boolean queryIsPlayerOnCaptainCooldown(long serverId, long queueId, long playerId) {
		String sql = "SELECT sum(captain) FROM "
				   + "(SELECT captain FROM PlayerGame JOIN Game ON Game.timestamp = PlayerGame.timestamp "
				   + "WHERE Game.serverId = ? AND queueId = ? AND playerId = ? AND completion_timestamp > 0 "
				   + "ORDER BY Game.timestamp DESC "
				   + "LIMIT ?)";
		boolean cooldown = false;
		int numOfGames = 1;
		
		try(PreparedStatement statement = _conn.prepareStatement(sql)){
			statement.setLong(1, serverId);
			statement.setLong(2, queueId);
			statement.setLong(3, playerId);
			statement.setInt(4, numOfGames);
			
			try(ResultSet rs = statement.executeQuery()){
				if(rs.next()){
					int count = rs.getInt(1);
					
					cooldown = count > 0;
				}
			}
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		return cooldown;
	}
}
