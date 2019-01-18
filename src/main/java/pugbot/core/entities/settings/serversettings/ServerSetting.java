package pugbot.core.entities.settings.serversettings;

import pugbot.core.Database;
import pugbot.core.entities.settings.Setting;

public abstract class ServerSetting<T> extends Setting<T> {

	public ServerSetting(long serverId, T value) {
		super(serverId, value);
	}

	public void save() {
		Database.updateServerSetting(getServerId(), getName(), getSaveString());
	}
}
