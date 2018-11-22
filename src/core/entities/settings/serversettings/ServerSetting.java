package core.entities.settings.serversettings;

import core.Database;
import core.entities.settings.Setting;

public abstract class ServerSetting<T> extends Setting<T> {

	public ServerSetting(T value) {
		super(value);
	}

	public void save() {
		Database.updateServerSetting(getServerId(), getName(), getValueString());
	}
}
