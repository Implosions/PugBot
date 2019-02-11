package pugbot.core.entities;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class MatchRecord {
	public final long timestamp;
	public final long serverId;
	public final long queueId;
	public final String queueName;
	public final long[] captainIds;
	public final String[] captainNames;
	private Integer result;
	
	public MatchRecord(long serverId, long timestamp, long[] captains, long queueId, String queueName, Integer result) {
		this.timestamp = timestamp;
		this.serverId = serverId;
		this.queueId = queueId;
		this.queueName = queueName;
		captainIds = captains;
		this.result = result;
		captainNames = new String[] { 
				getCaptainNameFromId(captainIds[0]),
				getCaptainNameFromId(captainIds[1]) };
	}
	
	public Integer getResult() {
		return result;
	}
	
	public void updateResult(Integer newResult) {
		result = newResult;
	}
	
	public boolean hasCaptainWithName(String name) {
		for(String c : captainNames) {
			if(c.toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	private String getCaptainNameFromId(long id) {
		String name = null;
		
		User u = ServerManager.getJDAInstance().getUserById(id);
		Guild g = ServerManager.getGuild(serverId);
		
		if(g.isMember(u)) {
			name = g.getMember(u).getEffectiveName();
		} else {
			name = u.getName();
		}
		
		return name;
	}
	
}
