package core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import core.Database;
import net.dv8tion.jda.core.entities.Member;

public class MatchMaker {
	private static final Random rng = new Random();
	private static final int FLAT_RATING_TOLERANCE = 1000;
	
	private final long serverId;
	private final long queueId;
	private final int minGamesPlayed;
	private List<Member> players;
	private List<Member> captainPool;
	private HashMap<Long,Integer> playerRatingMap = new HashMap<>();
	
	public MatchMaker(List<Member> players, long serverId, long queueId, int minGamesPlayed) {
		this.serverId = serverId;
		this.queueId = queueId;
		this.minGamesPlayed = minGamesPlayed;
		this.players = new ArrayList<>(players);
		
		for(Member p : players) {
			int rating = getPlayerRating(p);
			
			playerRatingMap.put(p.getUser().getIdLong(), rating);
		}
		
		captainPool = getCaptainPool();
		this.players.sort((Member p1,  Member p2) -> 
			getPlayerRatingFromMember(p2) - getPlayerRatingFromMember(p1));
	}
	
	private int getPlayerRating(Member player) {
		double rating = 0.0;
		
		for(Member m : players){
			long playerId = player.getUser().getIdLong();
			long playerToCompareId = m.getUser().getIdLong();
			
			if(playerId != playerToCompareId) {
				rating += Database.queryGetPickOrderDiff(serverId, playerId, playerToCompareId);
			}
		}
		
		return (int)(rating * 100);
	}
	
	private List<Member> getCaptainPool() {
		List<Member> captainPool = new ArrayList<Member>();
		
		for(Member m : players) {
			long memberId = m.getUser().getIdLong();
			int gamesPlayed = Database.queryGetPlayerTotalCompletedGames(
					serverId, memberId, queueId);
			boolean onCooldown = Database.queryIsPlayerOnCaptainCooldown(serverId, queueId, memberId);
			
			if(gamesPlayed >= minGamesPlayed && !onCooldown){
				captainPool.add(m);
			}
		}
		
		if(captainPool.size() < 2) {
			return new ArrayList<Member>(players);
		}
		
		return captainPool;
	}
	
	public Member[] getRandomizedCaptains() {
		Member[] captains;
		List<Member> uncheckedPlayers = new ArrayList<>(captainPool);
		int roll;
		int tolerance = FLAT_RATING_TOLERANCE;
		
		while(true) {
			if(uncheckedPlayers.isEmpty()) {
				uncheckedPlayers = new ArrayList<>(captainPool);
				tolerance *= 2;
			}
			
			captains = new Member[2];
			roll = rng.nextInt(uncheckedPlayers.size());
			captains[0] = uncheckedPlayers.get(roll);
			
			List<Member> possibleCoCaptains = getCaptainMatchesByRating(captains[0], tolerance);

			if(possibleCoCaptains.isEmpty()) {
				uncheckedPlayers.remove(captains[0]);
				continue;
			}
			
			roll = rng.nextInt(possibleCoCaptains.size());
			captains[1] = possibleCoCaptains.get(roll);
			break;
		}
		
		return captains;
	}

	private List<Member> getCaptainMatchesByRating(Member captain, int tolerance) {
		List<Member> matches = new ArrayList<Member>();
		int ratingToMatch = getPlayerRatingFromMember(captain);
		
		for(Member member : captainPool) {
			int memberRating = getPlayerRatingFromMember(member);
			int difference = Math.abs(ratingToMatch - memberRating);
			
			if(difference <= tolerance && captain != member) {
				matches.add(member);
			}
		}
		
		return matches;
	}
	
	public List<Member> getOrderedPlayerList() {
		return players;
	}
	
	private int getPlayerRatingFromMember(Member m) {
		return playerRatingMap.get(m.getUser().getIdLong());
	}
}
