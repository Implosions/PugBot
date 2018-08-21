package core.entities.settings;

import core.Database;
import core.entities.Queue;
import core.entities.Server;
import net.dv8tion.jda.core.entities.Category;

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
		return (Category)getSetting("MinGamesPlayedToCaptain").getValue();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void save(Setting setting) {
		Database.updateQueueSetting(getServer().getId(), queue.getId(), setting.getName(), setting.getSaveString());
	}
}
