package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.Database;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class CmdDeleteCommand extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		String cmdName = args[0].toLowerCase();

		if (!server.getCommandManager().doesCommandExist(cmdName)) {
			throw new InvalidUseException(String.format("Command '%s' does not exist", cmdName));
		}
		
		ICommand cmd = server.getCommandManager().getCommand(cmdName);
		
		if(cmd.getClass() != CustomCommand.class){
			throw new InvalidUseException(String.format("Command '%s' is not a custom command", cmdName));
		}

		server.getCommandManager().removeCommand(cmdName);
		Database.deleteCustomCommand(Long.valueOf(server.getId()), cmdName);

		return Utils.createMessage(String.format("Command '%s' deleted", cmdName));
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
		return "DeleteCommand";
	}

	@Override
	public String getDescription() {
		return "Deletes a custom command";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <command name>";
	}
}
