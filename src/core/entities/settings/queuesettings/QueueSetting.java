package core.entities.settings.queuesettings;

import core.Database;
import core.entities.settings.QueueSettingsManager;
import core.entities.settings.Setting;

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
