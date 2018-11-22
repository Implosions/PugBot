package core.entities.settings;

import java.util.List;

import core.Database;
import core.entities.Queue;
import core.entities.Server;
import core.entities.menus.MapPickMenuController.PickStyle;
import core.entities.settings.queuesettings.SettingMapCount;
import core.entities.settings.queuesettings.SettingMapPickingStyle;
import core.entities.settings.queuesettings.SettingMapPool;
import core.entities.settings.queuesettings.SettingRoleRestrictions;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Role;

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
		return (int)getSetting("MinGamesPlayedToCaptain").getValue();
	}
	
	public String getPickPattern(){
		return (String)getSetting("PickPattern").getValue();
	}
	
	public Category getVoiceChannelCategory(){
		return (Category)getSetting("VoiceChannelCategory").getValue();
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
