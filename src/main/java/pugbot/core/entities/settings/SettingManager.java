package pugbot.core.entities.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pugbot.core.entities.Server;
import pugbot.core.exceptions.InvalidUseException;

public abstract class SettingManager {
	
	private Server server;
	private HashMap<String, Setting<?>> settingMap = new HashMap<String, Setting<?>>();
	
	public SettingManager(Server server){
		this.server = server;
	}
	
	public Setting<?> getSetting(String settingName){
		return settingMap.get(settingName.toLowerCase());
	}
	
	public void addSetting(Setting<?> setting){
		setting.setManager(this);
		settingMap.put(setting.getName().toLowerCase(), setting);
	}
	
	public void setSetting(String settingName, String[] args){
		settingName = settingName.toLowerCase();
		
		if(!settingMap.containsKey(settingName)){
			throw new InvalidUseException(String.format("Setting '%s' does not exist", settingName));
		}
		Setting<?> setting = settingMap.get(settingName);
		
		setting.set(args);
		setting.save();
	}
	
	public Server getServer(){
		return server;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		List<ISetting> settings = new ArrayList<>();
		
		settings.addAll(settingMap.values());
		settings.sort((ISetting s1, ISetting s2) -> s1.getName().compareTo(s2.getName()));
		
		for(ISetting setting : settings){
			builder.append(setting.getName() + ": " + setting.getValueString() + System.lineSeparator());
		}
		
		return builder.toString();
	}
}
