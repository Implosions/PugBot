package core.commands;

import java.util.Arrays;

import core.Constants;
import core.Database;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdCreateCommand extends Command {

	public CmdCreateCommand(Server server) {
		this.name = Constants.CREATECOMMAND_NAME;
		this.helpMsg = Constants.CREATECOMMAND_HELP;
		this.description = Constants.CREATECOMMAND_DESC;
		this.adminRequired = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length < 2) {
			throw new BadArgumentsException();
		}

		String cmdName = args[0].toLowerCase();

		if (server.cmds.validateCommand(cmdName)) {
			throw new InvalidUseException(String.format("Command '%s' already exists", cmdName));
		}

		String cmdResponse = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

		server.cmds.addCommand(new CustomCommand(cmdName, cmdResponse));
		Database.insertServerCustomCommand(Long.valueOf(server.getId()), cmdName, cmdResponse);

		return Utils.createMessage(String.format("Command '%s' created", cmdName));
	}
}
