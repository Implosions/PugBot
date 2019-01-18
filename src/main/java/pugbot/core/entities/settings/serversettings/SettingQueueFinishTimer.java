package pugbot.core.entities.settings.serversettings;

import pugbot.core.exceptions.BadArgumentsException;

public class SettingQueueFinishTimer extends ServerSetting<Integer>{

	public SettingQueueFinishTimer(long serverId, Integer value) {
		super(serverId, value);
	}

	@Override
	public String getName() {
		return "QueueFinishTimer";
	}

	@Override
	public String getDescription() {
		return "The amount of time after a game is finished that players cannot automatically join another queue";
	}

	@Override
	public String getValueString() {
		return getValue() + " Seconds";
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
