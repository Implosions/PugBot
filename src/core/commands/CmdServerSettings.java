package core.commands;

import java.util.Arrays;

import core.entities.Server;
import core.entities.settings.ISetting;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdServerSettings extends Command {

	public CmdServerSettings(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {				
		if (args.length == 0) {
			response = Utils.createMessage("Server settings", server.getSettingsManager().toString(), true);
		} else if(args.length == 1){
			ISetting setting = server.getSettingsManager().getSetting(args[0]);
			
			response = Utils.createMessage("Server Settings",
					String.format("%s: %s%n%s", setting.getName(), setting.getValueString(), setting.getDescription()), true);
		} else {
			String settingName = args[0];
			String[] settingArgs = Arrays.copyOfRange(args, 1, args.length);
			
			server.getSettingsManager().setSetting(settingName, settingArgs);
			
			response = Utils.createMessage("Server Settings", settingName + " updated", true);
		}

		return response;
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return true;
	}

	@Override
	public String getName() {
		return "ServerSettings";
	}

	@Override
	public String getDescription() {
		return "Gets or sets this bot's server settings";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Lists all server settings\n" +
				getBaseCommand() + "<setting> - Lists a setting and its description\n" +
				getBaseCommand() + "<setting> <args> - Updates a setting";
	}

}
