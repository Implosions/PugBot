package core.util;

import java.util.List;

import core.Database;
import net.dv8tion.jda.core.entities.Member;

public class MatchMaker {
	
	public static Member getMatch(Member member, List<Member> memberPool){
		int ratingToMatch = getRating(member, memberPool);
		int closestMatchDifference = Integer.MAX_VALUE;
		Member closestMatch = null;
		
		
		for(Member m : memberPool){
			int rating = getRating(m, memberPool);
			int diff = Math.abs(ratingToMatch - rating);
			
			if(closestMatch == null || diff < closestMatchDifference){
				closestMatch = m;
				closestMatchDifference = diff;
			}
		}
		
		return closestMatch;
	}
	
	private static int getRating(Member member, List<Member> memberPool){
		double rating = 0.0;
		long serverId = member.getGuild().getIdLong();
		
		for(Member m : memberPool){
			if(m != member){
				rating += Database.queryGetPickOrderDiff(serverId, member.getUser().getIdLong(), m.getUser().getIdLong());
			}
		}
		
		return (int)rating;
	}
}
