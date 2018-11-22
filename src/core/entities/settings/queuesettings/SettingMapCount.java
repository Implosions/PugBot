package core.entities.settings.queuesettings;

import core.exceptions.BadArgumentsException;

public class SettingMapCount extends QueueSetting<Integer>{

	public SettingMapCount(Integer value) {
		super(value);
	}

	@Override
	public String getName() {
		return "MapCount";
	}

	@Override
	public String getDescription() {
		return "The number of maps to be played";
	}

	@Override
	public String getValueString() {
		return getSaveString() + " Maps";
	}

	@Override
	public String getSaveString() {
		return getValue().toString();
	}

	@Override
	public void set(String[] args) {
		if(args.length != 1){
			throw new BadArgumentsException("Only one argument is allowed");
		}
		
		int newValue;
		
		try{
			newValue = Integer.valueOf(args[0]);
		}catch(NumberFormatException ex){
			throw new BadArgumentsException("Value must be a valid integer");
		}
		
		setValue(newValue);
	}
	
}
