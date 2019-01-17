package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.CommandManager;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class CmdDisableCommand extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if(args.length != 1) {
			throw new BadArgumentsException();
		}
		
		String cmdName = args[0];
		CommandManager cmdManager = server.getCommandManager();
		
		if(!cmdManager.doesCommandExist(cmdName)) {
			throw new InvalidUseException("Command does not exist");
		}
		
		ICommand cmd = cmdManager.getCommand(cmdName);
		
		if(!cmdManager.isCommandEnabled(cmdName)) {
			throw new InvalidUseException(String.format("Command '%s' is already disabled", cmd.getName()));
		}
		
		if(cmd.isAdminRequired()) {
			throw new InvalidUseException("Cannot disable an admin command");
		}
		
		cmdManager.disableCommand(cmdName);
		
		return Utils.createMessage(String.format("`Command '%s' disabled`", cmd.getName()));
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
		return "DisableCommand";
	}

	@Override
	public String getDescription() {
		return "Disables a command from further use";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <cmd name>";
	}

}
