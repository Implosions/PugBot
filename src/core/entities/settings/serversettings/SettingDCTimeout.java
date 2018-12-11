package core.entities.settings.serversettings;

import core.exceptions.BadArgumentsException;

public class SettingDCTimeout extends ServerSetting<Integer> {
	
	public SettingDCTimeout(Integer value) {
		super(value);
	}

	@Override
	public String getName() {
		return "DCTimeout";
	}

	@Override
	public String getDescription() {
		return "The amount of time when a player disconnects until removal";
	}

	@Override
	public String getValueString() {
		return getValue() + " Minutes";
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
		
		if(newValue < 0){
			throw new BadArgumentsException("Value must be positive");
		}
		
		setValue(newValue);
	}

	@Override
	public String getSaveString() {
		return getValue().toString();
	}

}
