package bullybot.classfiles;

import java.util.HashMap;
import java.util.List;

import bullybot.classfiles.util.Functions;
import net.dv8tion.jda.core.entities.Guild;

public class ServerManager {
	private static HashMap<String, Server> serverMap;
	
	public ServerManager(List<Guild> guilds){
		Functions.createFile("app_data");
		serverMap = new HashMap<String, Server>();
		guilds.forEach((g) -> serverMap.put(g.getId(), new Server(g.getId(), g)));
	}
	
	public static Server getServer(String id){
		return serverMap.get(id);
	}
	
	public static void addNewServer(Guild guild){
		serverMap.put(guild.getId(), new Server(guild.getId(), guild));
	}
	
	public static void removeServer(Guild guild){
		serverMap.remove(guild.getId());
	}
}
