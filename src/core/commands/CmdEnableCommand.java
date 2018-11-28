package core.commands;

import core.entities.CommandManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdEnableCommand extends Command {

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
		
		if(cmdManager.isCommandEnabled(cmdName)) {
			throw new InvalidUseException(String.format("Command '%s' is not disabled", cmd.getName()));
		}
		
		cmdManager.enableCommand(cmdName);
		
		return Utils.createMessage(String.format("`Command '%s' enabled`", cmd.getName()));
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
		return "EnableCommand";
	}

	@Override
	public String getDescription() {
		return "Enables a disabled command";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <cmd name>";
	}

}
