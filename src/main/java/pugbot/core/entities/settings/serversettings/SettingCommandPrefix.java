package pugbot.core.entities.settings.serversettings;

import pugbot.core.exceptions.InvalidUseException;

public class SettingCommandPrefix extends ServerSetting<String> {

	public SettingCommandPrefix(long serverId, String value) {
		super(serverId, value);
	}

	@Override
	public String getName() {
		return "CommandPrefix";
	}

	@Override
	public String getDescription() {
		return "The prefix used to invoke commands";
	}

	@Override
	public String getValueString() {
		return getValue();
	}

	@Override
	public String getSaveString() {
		return getValue();
	}

	@Override
	public void set(String[] args) {
		if(args.length > 1) {
			throw new InvalidUseException("The command prefix cannot have spaces");
		}
		
		String prefix = args[0];
		
		if(prefix.length() > 3) {
			throw new InvalidUseException("The command prefix cannot be longer than 3 characters");
		}
		
		setValue(prefix);
	}
	
}
