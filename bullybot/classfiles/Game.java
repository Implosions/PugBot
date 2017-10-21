package bullybot.classfiles;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import bullybot.classfiles.util.Functions;
import net.dv8tion.jda.core.entities.User;

public class Game {
	private String guildId;
	private String name;
	private Long timestamp;
	private ArrayList<User> players;
	private String[] captains = new String[]{"",""};
	
	public Game(String id, String name, ArrayList<User> players){
		this.guildId = id; 
		this.name = name;
		this.timestamp = System.currentTimeMillis();
		this.players = new ArrayList<User>(players);
		randomizeCaptains();
		writeToFile();
	}
	
	public ArrayList<User> getPlayers(){
		return players;
	}
	
	public Long getTimestamp(){
		return timestamp;
	}
	
	public User getPlayer(String name){
		for(User u : players){
			if(u.getName().equalsIgnoreCase(name)){
				return u;
			}
		}
		return null;
	}
	
	public boolean containsPlayer(String name) {
		for(User u : players){
			if(u.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	private void randomizeCaptains(){
		Random random = new Random();
		
		if(players.size() == 1){
			captains[0] = players.get(0).getId();
			captains[1] = players.get(0).getId();
			return;
		}
		captains[0] = players.get(random.nextInt(players.size())).getId();
		while(captains[1].isEmpty()){
			Integer i = random.nextInt(players.size());
			if(!players.get(i).getId().equals(captains[0])){
				captains[1] = players.get(i).getId();
			}
		}
	}
	
	public String[] getCaptains(){
		return captains;
	}
	
	public String getName(){
		return name;
	}
	
	private void writeToFile(){
		String s = String.format("%s/%s/%s", "app_data", guildId, "games.txt");
		Functions.createFile(s);
		try{
			System.out.println("Logging game to file...");
			PrintWriter writer = new PrintWriter(new FileOutputStream(s, true));
			writer.format("%s %s %3$tk:%3$tM:%3$tS %3$tD%n", name, players.toString(), new Date(timestamp));
			writer.close();
			System.out.println("Game logged");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
