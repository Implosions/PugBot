package core.entities;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;

public class PUGTeam {
	
	private Member captain;
	private List<Member> players;
	
	public PUGTeam() {
		players = new ArrayList<Member>();
	}
	
	public PUGTeam(Member captain) {
		super();
		this.captain = captain;
	}
	
	public PUGTeam(Member captain, List<Member> players) {
		this.captain = captain;
		this.players = players;
	}
	
	public Member getCaptain() {
		return captain;
	}
	
	public void setCaptain(Member captain) {
		this.captain = captain;
	}
	
	public List<Member> getPlayers(){
		return players;
	}
	
	public void addPlayer(Member player) {
		players.add(player);
	}
	
	public void removePlayer(Member player) {
		players.remove(player);
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(captain.getEffectiveName() + ": ");
		
		if(players.size() > 0){
			for(Member player : players){
				builder.append(player.getEffectiveName() + ", ");
			}
			
			builder.delete(builder.length() - 2, builder.length());
		}
		
		return builder.toString();
	}
}
