package pugbot.core.entities.settings;

import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Role;
import pugbot.core.Database;
import pugbot.core.entities.Queue;
import pugbot.core.entities.Server;
import pugbot.core.entities.menus.MapPickMenuController.PickStyle;
import pugbot.core.entities.settings.queuesettings.SettingMapCount;
import pugbot.core.entities.settings.queuesettings.SettingMapPickingStyle;
import pugbot.core.entities.settings.queuesettings.SettingMapPool;
import pugbot.core.entities.settings.queuesettings.SettingMinGamesPlayedToCaptain;
import pugbot.core.entities.settings.queuesettings.SettingPickPattern;
import pugbot.core.entities.settings.queuesettings.SettingRoleRestrictions;
import pugbot.core.entities.settings.queuesettings.SettingVoiceChannelCategory;

public class QueueSettingsManager extends SettingManager {

	private Queue queue;
	
	public QueueSettingsManager(Server server, Queue queue) {
		super(server);
		this.queue = queue;
		Database.loadQueueSettings(this);
	}
	
	public Queue getParentQueue(){
		return queue;
	}

	public int getMinGamesPlayedToCaptain(){
		return ((SettingMinGamesPlayedToCaptain)getSetting("MinGamesPlayedToCaptain")).getValue();
	}
	
	public String getPickPattern(){
		return ((SettingPickPattern)getSetting("PickPattern")).getValue();
	}
	
	public Category getVoiceChannelCategory(){
		return ((SettingVoiceChannelCategory)getSetting("VoiceChannelCategory")).getValue();
	}
	
	public List<Role> getRoleRestrictions(){
		return ((SettingRoleRestrictions)getSetting("RoleRestrictions")).getValue();
	}
	
	public List<String> getMapPool(){
		return ((SettingMapPool)getSetting("MapPool")).getValue();
	}
	
	public int getMapCount() {
		return ((SettingMapCount)getSetting("MapCount")).getValue();
	}
	
	public PickStyle getPickStyle() {
		return ((SettingMapPickingStyle)getSetting("MapPickingStyle")).getValue();
	}
}
