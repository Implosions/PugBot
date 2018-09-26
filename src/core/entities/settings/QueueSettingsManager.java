package core.entities.settings;

import java.util.List;

import core.Database;
import core.entities.Queue;
import core.entities.Server;
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

	@Override
	protected void save(Setting<?> setting) {
		if(setting.getClass() == SettingRoleRestrictions.class){
			Database.updateQueueRoleRestrictions(getServer().getId(), queue.getId(), ((SettingRoleRestrictions)setting).getValue());
		}else{
			Database.updateQueueSetting(getServer().getId(), queue.getId(), setting.getName(), setting.getSaveString());
		}
	}
}
