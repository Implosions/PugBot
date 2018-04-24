package core.entities;

import java.util.List;

import core.exceptions.InvalidUseException;

public abstract class Settings {
	protected List<Setting> settingsList;
	protected long serverId;
	
	public Settings(long serverId){
		this.serverId = serverId;
	}
	
	public Setting getSetting(String settingName){
		for(Setting setting : settingsList){
			if(settingName.equalsIgnoreCase(setting.getName())){
				return setting;
			}
		}
		throw new InvalidUseException("Setting does not exist");
	}
	
	public void set(String settingName, String value){
		Setting setting = getSetting(settingName);
		try{
			if(setting.getValue().getClass() == Integer.class){
				int newVal = Integer.valueOf(value);
				if(newVal > 0){
					setting.setValue(newVal);
				}else{
					throw new InvalidUseException("Value must be greater than 0");
				}
				
			}else if(setting.getValue().getClass() == Boolean.class){
				setting.setValue(Boolean.valueOf(value));
			}else{
				setting.setValue(value.toLowerCase());
			}
			
		}catch(NumberFormatException ex){
			throw new InvalidUseException("New value is of the wrong type");
		}
	}
	
	public List<Setting> getSettingsList(){
		return settingsList;
	}
}
