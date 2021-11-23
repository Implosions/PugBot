package pugbot.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class PUGTeam {
	
	private Member captain;
	private HashMap<Member, Integer> playerMap;
	private Channel voiceChannel;
	
	public PUGTeam() {
		playerMap = new HashMap<>();
	}
	
	public PUGTeam(Member captain) {
		this();
		this.captain = captain;
	}
	
	public Member getCaptain() {
		return captain;
	}
	
	public void setCaptain(Member captain) {
		this.captain = captain;
	}
	
	public List<Member> getPlayers(){
		List<Member> players = new ArrayList<>(playerMap.keySet());
		
		players.sort((Member p1, Member p2) -> getNullCheckedVal(p1) - getNullCheckedVal(p2));
		
		return players;
	}
	
	public void addPlayer(Member player, Integer pickOrder) {
		playerMap.put(player, pickOrder);
	}
	
	public void removePlayer(Member player) {
		playerMap.remove(player);
	}

	public void clearPlayers() {
		playerMap.clear();
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(captain.getEffectiveName() + ": ");
		
		if(playerMap.size() > 0) {
			for(Member player : getPlayers()) {
				builder.append(player.getEffectiveName() + ", ");
			}
			
			builder.delete(builder.length() - 2, builder.length());
		}
		
		return builder.toString();
	}
	
	public void createVoiceChannel(Category category) {
		if(voiceChannel != null) {
			updateVoiceChannel();
			return;
		}
		
		try{
			String name = "Team " + captain.getEffectiveName();
			
			Guild guild = captain.getGuild();
			voiceChannel = guild.getController().createVoiceChannel(name).complete();
				
			voiceChannel.getManager().setParent(category).queue();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void updateVoiceChannel() {
		if(voiceChannel != null) {
			voiceChannel.getManager().setName("Team " + captain.getEffectiveName()).queue();
		}
	}
	
	public void deleteVoiceChannel() {
		if(voiceChannel != null){
			try{
				voiceChannel.delete().queue();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public Integer getPickOrder(Member player) {
		return playerMap.get(player);
	}
	
	private int getNullCheckedVal(Member m) {
		Integer val = getPickOrder(m);
		
		if(val == null) {
			return Integer.MAX_VALUE;
		}
		
		return val;
	}
}
