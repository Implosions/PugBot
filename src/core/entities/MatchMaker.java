package core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import core.Database;
import net.dv8tion.jda.core.entities.Member;

public class MatchMaker {
	private static final Random rng = new Random();
	private static final int START_TOLERANCE_PERCENT = 10;
	private static final int FLAT_RATING_TOLERANCE = 1000;
	
	private final long serverId;
	private final long queueId;
	private final int minGamesPlayed;
	private List<Member> players;
	private HashMap<Member,Integer> playerRatingMap = new HashMap<>();
	
	public MatchMaker(List<Member> players, long serverId, long queueId, int minGamesPlayed) {
		this.serverId = serverId;
		this.queueId = queueId;
		this.minGamesPlayed = minGamesPlayed;
		this.players = new ArrayList<>(players);
		
		for(Member p : players) {
			int rating = getPlayerRating(p);
			
			playerRatingMap.put(p, rating);
		}
		
		this.players.sort((Member p1,  Member p2) -> playerRatingMap.get(p1) -  playerRatingMap.get(p2));
	}
	
	private int getPlayerRating(Member player) {
		double rating = 0.0;
		long playerId = player.getUser().getIdLong();
		
		for(Member m : players){
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
			boolean onCooldown = Database.queryIsPlayerOnCaptainCooldown(serverId, queueId, memberId, players.size());
			
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
		List<Member> eligiblePlayers = getCaptainPool();
		List<Member> captainPool = new ArrayList<>(eligiblePlayers);
		int roll;
		int tolerance = START_TOLERANCE_PERCENT;
		
		while(true) {
			if(captainPool.isEmpty()) {
				captainPool = new ArrayList<>(eligiblePlayers);
				tolerance *= 2;
			}
			
			captains = new Member[2];
			roll = rng.nextInt(captainPool.size());
			captains[0] = captainPool.get(roll);
			
			List<Member> possibleCoCaptains = getCaptainMatchesByRating(eligiblePlayers, captains[0], tolerance);

			if(possibleCoCaptains.isEmpty()) {
				captainPool.remove(captains[0]);
				continue;
			}
			
			roll = rng.nextInt(possibleCoCaptains.size());
			captains[1] = possibleCoCaptains.get(roll);
			break;
		}
		
		return captains;
	}

	private List<Member> getCaptainMatchesByRating(List<Member> captainPool, Member captain, int tolerance) {
		List<Member> matches = new ArrayList<Member>();
		int ratingToMatch = playerRatingMap.get(captain);
		int matchLatitude = (int)(ratingToMatch * (double)(tolerance / 100)) + FLAT_RATING_TOLERANCE;
		
		for(Member member : captainPool) {
			int memberRating = playerRatingMap.get(member);
			int difference = Math.abs(ratingToMatch - memberRating);
			
			if(difference <= matchLatitude && captain != member) {
				matches.add(member);
			}
		}
		
		return matches;
	}
	
	public List<Member> getOrderedPlayerList() {
		return players;
	}
}
