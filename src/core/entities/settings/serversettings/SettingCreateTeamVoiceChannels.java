package core.entities.settings.serversettings;

import core.exceptions.BadArgumentsException;

public class SettingCreateTeamVoiceChannels extends ServerSetting<Boolean> {
	
	public SettingCreateTeamVoiceChannels(Boolean value) {
		super(value);
	}

	@Override
	public String getName() {
		return "CreateTeamVoiceChannels";
	}

	@Override
	public String getDescription() {
		return "Creates a voice channel for each team when a game starts, and removes it after completion";
	}

	@Override
	public String getValueString() {
		return getValue().toString();
	}

	@Override
	public void set(String[] args) {
		if(args.length != 1){
			throw new BadArgumentsException("Only one argument is allowed");
		}
		
		boolean newValue = Boolean.valueOf(args[0]);
		
		setValue(newValue);
	}

	@Override
	public String getSaveString() {
		return getValue().toString();
	}

}
