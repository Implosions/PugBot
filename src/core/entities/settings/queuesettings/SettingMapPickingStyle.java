package core.entities.settings.queuesettings;

import core.entities.menus.MapPickMenuController.PickStyle;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;

public class SettingMapPickingStyle extends QueueSetting<PickStyle>{
	
	public SettingMapPickingStyle(Integer value) {
		super(PickStyle.values()[value]);
	}

	@Override
	public String getName() {
		return "MapPickingStyle";
	}

	@Override
	public String getDescription() {
		return "The style that choosing maps follows\n"
				+ "Pick - Each captain chooses a map until the MapCount is reached\n"
				+ "Ban - Each captain bans a map until the MapCount is reached";
	}

	@Override
	public String getValueString() {
		return getValue().toString();
	}

	@Override
	public String getSaveString() {
		return String.valueOf(getValue().ordinal());
	}

	@Override
	public void set(String[] args) {
		if(args.length != 1){
			throw new BadArgumentsException("Only one argument is allowed");
		}
		
		switch(args[0].toLowerCase()) {
		case "pick": setValue(PickStyle.PICK); break;
		case "ban": setValue(PickStyle.BAN); break;
		default: throw new InvalidUseException("Value must be 'pick' or 'ban'");
		}
	}
	
}
