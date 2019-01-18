package pugbot.core.entities.settings.queuesettings;

import pugbot.core.Database;
import pugbot.core.entities.settings.Setting;

public abstract class QueueSetting<T> extends Setting<T> {

	private long queueId;
	
	public QueueSetting(long serverId, long queueId, T value) {
		super(serverId, value);
		this.queueId = queueId;
	}
	
	public long getQueueId() {
		return queueId;
	}

	public void save() {		
		Database.updateQueueSetting(getServerId(), getQueueId(), getName(), getSaveString());
	}
}
