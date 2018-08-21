package core.commands;

import java.util.Arrays;

import core.Constants;
import core.entities.Server;
import core.entities.settings.ISetting;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdServerSettings extends Command {

	public CmdServerSettings(Server server) {
		this.name = Constants.SERVERSETTINGS_NAME;
		this.helpMsg = Constants.SERVERSETTINGS_HELP;
		this.description = Constants.SERVERSETTINGS_DESC;
		this.adminRequired = true;
		this.server = server;
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

}
