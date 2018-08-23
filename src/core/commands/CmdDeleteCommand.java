package core.commands;

import core.Constants;
import core.Database;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDeleteCommand extends Command {

	public CmdDeleteCommand(Server server) {
		this.name = Constants.DELETECOMMAND_NAME;
		this.helpMsg = Constants.DELETECOMMAND_HELP;
		this.description = Constants.DELETECOMMAND_DESC;
		this.adminRequired = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		String cmdName = args[0].toLowerCase();

		if (!server.cmds.getCustomCmds().contains(cmdName)) {
			throw new InvalidUseException(String.format("'%s' is not a custom command", cmdName));
		}

		server.cmds.removeCommand(cmdName);
		Database.deleteCustomCommand(Long.valueOf(server.getId()), cmdName);

		return Utils.createMessage(String.format("Command '%s' deleted", cmdName));
	}
}
