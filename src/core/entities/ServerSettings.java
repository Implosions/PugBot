package core.entities;

import core.Database;

public class ServerSettings extends Settings{
	
	public ServerSettings(long serverId) {
		super(serverId);
		settingsList = Database.getServerSettings(serverId);
	}
	
	@Override
	public void set(String settingName, String value){
		super.set(settingName, value);
		Database.updateServerSetting(serverId, settingName, value);
	}
	
	public int getDCTimeout(){
		return (int)getSetting("DCTimeout").getValue();
	}
	
	public int getAFKTimeout(){
		return (int)getSetting("AFKTimeout").getValue();
	}
	
	public String getPUGChannel(){
		return (String)getSetting("PUGChannel").getValue();
	}
	
	public int getMinNumberOfGamesToCaptain(){
		return (int)getSetting("minNumberOfGamesToCaptain").getValue();
	}
	
	public int getQueueFinishTimer(){
		return (int)getSetting("queueFinishTimer").getValue();
	}
	
	public boolean postTeamsToPugChannel(){
		return (boolean)getSetting("postPickedTeamsToPugChannel").getValue();
	}
	
	public boolean createDiscordVoiceChannels(){
		return (boolean)getSetting("createDiscordVoiceChannels").getValue();
	}
}
