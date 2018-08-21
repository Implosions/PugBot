package core.entities.settings;

import core.Database;
import core.entities.Server;
import net.dv8tion.jda.core.entities.TextChannel;

public class ServerSettingsManager extends SettingManager {

	public ServerSettingsManager(Server server) {
		super(server);
		Database.loadServerSettings(this);
	}
	
	public int getAFKTimeout(){
		return (int)getSetting("AFKTimeout").getValue();
	}
	
	public int getDCTimeout(){
		return (int)getSetting("DCTimeout").getValue();
	}
	
	public boolean getCreateTeamVoiceChannels(){
		return (boolean)getSetting("CreateTeamVoiceChannels").getValue();
	}
	
	public TextChannel getPUGChannel(){
		return (TextChannel)getSetting("PUGChannel").getValue();
	}
	
	public int getQueueFinishTimer(){
		return (int)getSetting("QueueFinishTimer").getValue();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void save(Setting setting) {
		Database.updateServerSetting(getServer().getId(), setting.getName(), setting.getSaveString());
	}
}
