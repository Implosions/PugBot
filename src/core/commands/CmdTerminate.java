package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdTerminate extends Command {
	public CmdTerminate(Server server) {
		this.name = Constants.TERMINATE_NAME;
		this.helpMsg = Constants.TERMINATE_HELP;
		this.description = Constants.TERMINATE_DESC;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (!caller.getUser().getId().equals(Constants.OWNER_ID)) {
			throw new InvalidUseException("You do not have the required permissions");
		}

		System.exit(0);

		return null;
	}
}
