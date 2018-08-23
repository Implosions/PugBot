package core.entities.settings.queuesettings;

import core.entities.settings.Setting;
import core.exceptions.BadArgumentsException;

public class SettingPickPattern extends Setting<String> {

	public SettingPickPattern(String value) {
		super(value);
	}

	@Override
	public String getName() {
		return "PickPattern";
	}

	@Override
	public String getDescription() {
		return "The pattern that the picking order will follow\n"
				+ "Format: <number of initial picks (optional)> <sequence of pick counts>\n"
				+ "Example: 1122";
	}

	@Override
	public String getValueString() {
		return getValue();
	}

	@Override
	public void set(String[] args) {
		if(args.length > 2){
			throw new BadArgumentsException("Invalid format");
		}
		
		for(String arg : args){
			try{
				Long.parseLong(arg);
			}catch(NumberFormatException ex){
				throw new BadArgumentsException("Values must be valid integers");
			}
		}
		
		setValue(String.join(" ", args));
	}

	@Override
	public String getSaveString() {
		return getValue();
	}

}
