package core.entities;

import java.util.HashMap;

import core.entities.timers.AFKTimer;
import core.util.Utils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

public class ServerManager {
	private static ServerManager manager;
	private static JDA jdaInstance;
	
	private HashMap<Long, Server> servers;
	
	private ServerManager(){
		servers = new HashMap<Long, Server>();
		
		for(Guild guild : getJDAInstance().getGuilds()){
			servers.put(guild.getIdLong(), new Server(guild));
		}
		
		new AFKTimer(this).start();
	}
	
	public ServerManager(JDA jda){
		
		Utils.createDir("app_data");
		
		jdaInstance = jda;
		manager = new ServerManager();
	}
	
	public static Server getServer(long serverId){
		return manager.servers.get(serverId);
	}
	
	public static Server getServer(String serverId){
		return manager.servers.get(Long.valueOf(serverId));
	}
	
	public static void addNewServer(Guild guild){
		manager.servers.put(guild.getIdLong(), new Server(guild));
	}
	
	public static void removeServer(long serverId){
		@SuppressWarnings("unused")
		Server server = getServer(serverId);
		
		server = null;
		manager.servers.remove(serverId);
	}
	
	public static Guild getGuild(long guildId){
		return jdaInstance.getGuildById(guildId);
	}
	
	public static Guild getGuild(String guildId){
		return jdaInstance.getGuildById(guildId);
	}
	
	public static JDA getJDAInstance(){
		return jdaInstance;
	}
	
	public void checkServerActivityLists(){
		for(Server server : servers.values()){
			server.checkActivityList();
		}
	}
}
