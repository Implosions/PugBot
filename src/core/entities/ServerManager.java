package core.entities;

import java.util.ArrayList;
import java.util.List;

import core.util.Utils;
import net.dv8tion.jda.core.entities.Guild;

public class ServerManager {
	private static List<Server> servers = new ArrayList<Server>();
	private static List<Guild> guilds;
	
	public ServerManager(List<Guild> guilds){
		Utils.createDir("app_data");
		ServerManager.guilds = guilds;
		guilds.forEach((g) -> {servers.add(new Server(g));
									servers.get(servers.size() - 1).getQueueManager().updateTopic();
								});
	}
	
	public static Server getServer(String id){
		for(Server s : servers){
			if(s.getid().equals(id)){
				return s;
			}
		}
		return null;
	}
	
	public static void addNewServer(Guild guild){
		servers.add(new Server(guild));
		servers.get(servers.size() - 1).getQueueManager().updateTopic();
	}
	
	public static void removeServer(Guild guild){
		servers.remove(getServer(guild.getId()));
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
