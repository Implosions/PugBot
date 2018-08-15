package core.commands;

import core.Constants;
import core.entities.Server;
import core.entities.Setting;
import core.exceptions.BadArgumentsException;
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
		if (args.length > 2) {
			throw new BadArgumentsException();
		}

		if (args.length == 0) {
			String s = "";
			for (Setting setting : server.getSettings().getSettingsList()) {
				s += String.format("%s = %s", setting.getName(), setting.getValue().toString());

				if (setting.getDescriptor() != null) {
					s += String.format(" %s%n", setting.getDescriptor());
				} else {
					s += "\n";
				}
			}

			response = Utils.createMessage("Server settings", s, true);

		} else if (args.length == 1) {
			Setting setting = server.getSettings().getSetting(args[0]);
			String s = String.format("%s = %s", setting.getName(), setting.getValue().toString());

			if (setting.getDescriptor() != null) {
				s += String.format(" %s%n", setting.getDescriptor());
			} else {
				s += "\n";
			}

			s += setting.getDescription();

			response = Utils.createMessage("Server setting", s, true);

		} else if (args.length == 2) {
			server.getSettings().set(args[0], args[1]);

			response = Utils.createMessage("Server setting", "Setting updated", true);
		}

		return response;
	}

}
