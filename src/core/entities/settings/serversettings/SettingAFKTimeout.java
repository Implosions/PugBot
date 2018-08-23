package core.entities.settings.serversettings;

import core.entities.settings.Setting;
import core.exceptions.BadArgumentsException;

public class SettingAFKTimeout extends Setting<Integer> {
	
	public SettingAFKTimeout(int value) {
		super(value);
	}

	@Override
	public String getName() {
		return "AFKTimeout";
	}

	@Override
	public String getDescription() {
		return "The amount of time a player can be inactive while in queue until being removed";
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
		
		if(newValue < 1){
			throw new BadArgumentsException("Value must be greater than 1");
		}
		
		setValue(newValue);
	}

	@Override
	public String getSaveString() {
		return getValue().toString();
	}
}
