package core.util;

import java.util.ArrayList;
import java.util.List;

import core.Database;
import net.dv8tion.jda.core.entities.User;

public class MatchMaker {
	
	String guildId;
	List<Ranking> rankings = new ArrayList<Ranking>();
	
	public MatchMaker(String guildId, List<User> players){
		this.guildId = guildId;
		for(User p : players){
			rankings.add(new Ranking(p));
		}
		calcRank();
	}
	
	private void calcRank(){
		for(Ranking ranking : rankings){
			double score = 0.0;
			for(Ranking r : rankings){
				if(r != ranking){
					Double pickDiff = Database.queryGetPickOrderDiff(Long.valueOf(guildId), ranking.player.getIdLong(), r.player.getIdLong());

					if(pickDiff > 0){
						score += pickDiff;
					}
				}
			}
			
			ranking.score = (int)(score * 100);
		}
	}
	
	public User getMatch(User captain){
		Ranking captainRanking = getRanking(captain);
		Ranking closestMatch = null;
		
		for(Ranking r : rankings){
			if(r != captainRanking){
				if(closestMatch != null){
					if(captainRanking.getDifference(r) > r.score){
						closestMatch = r;
					}
				}else{
					closestMatch = r;
				}
			}
		}
		return closestMatch.player;
	}
	
	private Ranking getRanking(User player){
		for(Ranking ranking : rankings){
			if(ranking.player == player){
				return ranking;
			}
		}
		return null;
	}
	
	private class Ranking{
		private User player;
		private Integer score;
		
		public Ranking(User player){
			this.player = player;
			this.score = 0;
		}
		
		private Integer getDifference(Ranking r){
			return Math.abs(score - r.score);
		}
	}
}
