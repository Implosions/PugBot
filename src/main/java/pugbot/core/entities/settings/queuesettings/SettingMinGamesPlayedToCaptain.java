package pugbot.core.entities.settings.queuesettings;

import pugbot.core.exceptions.BadArgumentsException;

public class SettingMinGamesPlayedToCaptain extends QueueSetting<Integer>{

	public SettingMinGamesPlayedToCaptain(Integer value) {
		super(value);
	}

	@Override
	public String getName() {
		return "MinGamesPlayedToCaptain";
	}

	@Override
	public String getDescription() {
		return "The minumum amount of games played to be eligible to captain";
	}

	@Override
	public String getValueString() {
		return getValue() + " Games";
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
