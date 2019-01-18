package pugbot.core.entities.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pugbot.core.Database;
import pugbot.core.entities.Server;
import pugbot.core.entities.settings.serversettings.SettingAFKTimeout;
import pugbot.core.entities.settings.serversettings.SettingCommandPrefix;
import pugbot.core.entities.settings.serversettings.SettingCreateTeamVoiceChannels;
import pugbot.core.entities.settings.serversettings.SettingDCTimeout;
import pugbot.core.entities.settings.serversettings.SettingPUGChannel;
import pugbot.core.entities.settings.serversettings.SettingQueueFinishTimer;

public class ServerSettingsManager extends SettingManager {

	public ServerSettingsManager(Server server) {
		super(server);
		Database.loadServerSettings(this);
	}
	
	public int getAFKTimeout(){
		return ((SettingAFKTimeout)getSetting("AFKTimeout")).getValue();
	}
	
	public int getDCTimeout(){
		return ((SettingDCTimeout)getSetting("DCTimeout")).getValue();
	}
	
	public boolean getCreateTeamVoiceChannels(){
		return ((SettingCreateTeamVoiceChannels)getSetting("CreateTeamVoiceChannels")).getValue();
	}
	
	public TextChannel getPUGChannel(){
		return ((SettingPUGChannel)getSetting("PUGChannel")).getValue();
	}
	
	public int getQueueFinishTimer(){
		return ((SettingQueueFinishTimer)getSetting("QueueFinishTimer")).getValue();
	}
	
	public String getCommandPrefix() {
		return ((SettingCommandPrefix)getSetting("CommandPrefix")).getValue();
	}
}
