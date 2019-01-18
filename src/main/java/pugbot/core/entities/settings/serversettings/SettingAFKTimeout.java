package pugbot.core.entities.settings.serversettings;

import pugbot.core.exceptions.BadArgumentsException;

public class SettingAFKTimeout extends ServerSetting<Integer> {
	
	public SettingAFKTimeout(long serverId, int value) {
		super(serverId, value);
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
