package core.entities;

import java.util.HashMap;
import java.util.List;

import core.util.Functions;
import net.dv8tion.jda.core.entities.Guild;

public class ServerManager {
	private static HashMap<String, Server> serverMap;
	private static List<Guild> guilds;
	
	public ServerManager(List<Guild> guilds){
		Functions.createDir("app_data");
		ServerManager.guilds = guilds;
		serverMap = new HashMap<String, Server>();
		guilds.forEach((g) -> {serverMap.put(g.getId(), new Server(g));
									getServer(g.getId()).getQueueManager().updateTopic();
								});
	}
	
	public static Server getServer(String id){
		return serverMap.get(id);
	}
	
	public static void addNewServer(Guild guild){
		serverMap.put(guild.getId(), new Server(guild));
	}
	
	public static void removeServer(Guild guild){
		serverMap.remove(guild.getId());
	}
	
	public static Guild getGuild(String id){
		for(Guild g : guilds){
			if(g.getId().equals(id)){
				return g;
			}
		}
		return null;
	}
}
