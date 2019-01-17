package pugbot.core.entities.settings.queuesettings;

import pugbot.core.Database;
import pugbot.core.entities.settings.QueueSettingsManager;
import pugbot.core.entities.settings.Setting;

public abstract class QueueSetting<T> extends Setting<T> {

	public QueueSetting(T value) {
		super(value);
	}
	
	public long getQueueId() {
		QueueSettingsManager mgr = (QueueSettingsManager)manager;
		
		return mgr.getParentQueue().getId();
	}

	public void save() {		
		Database.updateQueueSetting(getServerId(), getQueueId(), getName(), getSaveString());
	}
}
