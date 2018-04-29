package core.entities;

import core.Database;

public class QueueSettings extends Settings{

	private int id;
	
	public QueueSettings(long serverId, int id) {
		super(serverId);
		this.id = id;
		settingsList = Database.getQueueSettings(serverId, id);
	}
	
	@Override
	public void set(String settingName, String value){
		super.set(settingName, value);
		Database.updateQueueSetting(serverId, id, settingName, value);
	}
	
	public int getMinNumberOfGamesPlayedToCaptain(){
		return (int)getSetting("minNumberOfGamesToCaptain").getValue();
	}
	
	public boolean randomizeCaptains(){
		return (boolean)getSetting("randomizeCaptains").getValue();
	}
	
	public boolean snakePick(){
		return (boolean)getSetting("snakePick").getValue();
	}
	
	public long getVoiceChannelCategoryId(){
		return (long)getSetting("voiceChannelCategoryId").getValue();
	}
}
