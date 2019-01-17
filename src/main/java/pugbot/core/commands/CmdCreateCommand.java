package pugbot.core.commands;

import java.util.Arrays;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.Database;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class CmdCreateCommand extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length < 2) {
			throw new BadArgumentsException();
		}

		String cmdName = args[0].toLowerCase();

		if (server.getCommandManager().doesCommandExist(cmdName)) {
			throw new InvalidUseException(String.format("Command '%s' already exists", cmdName));
		}

		String cmdResponse = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

		server.getCommandManager().addCommand(new CustomCommand(server, cmdName, cmdResponse));
		Database.insertServerCustomCommand(Long.valueOf(server.getId()), cmdName, cmdResponse);

		return Utils.createMessage(String.format("Command '%s' created", cmdName));
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "CreateCommand";
	}

	@Override
	public String getDescription() {
		return "Creates a new command that will output a set message when invoked";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <command name> <command output>";
	}
}
