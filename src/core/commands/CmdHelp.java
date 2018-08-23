package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdHelp extends Command {

	public CmdHelp(Server server) {
		this.helpMsg = Constants.HELP_HELP;
		this.description = Constants.HELP_DESC;
		this.name = Constants.HELP_NAME;
		this.dm = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		String message = "";

		if (args.length == 0) {
			message += "Commands:\n\n";
			Command cmdObj;

			// List commands
			for (String cmdName : server.cmds.getCmds()) {
				cmdObj = server.cmds.getCommandObj(cmdName);
				if (!cmdName.equals(Constants.TERMINATE_NAME)) {
					message += String.format("!%s - %s%n", cmdObj.getName(), cmdObj.getDescription());
				}
			}

			// List custom commands
			for (String cmd : server.cmds.getCustomCmds()) {
				cmdObj = server.cmds.getCommandObj(cmd);
				message += String.format("!%s%n", cmdObj.getName());
			}

			// List admin commands
			if (server.isAdmin(caller)) {
				message += "\nAdmin commands:\n\n";
				for (String cmd : server.cmds.getAdminCmds()) {
					cmdObj = server.cmds.getCommandObj(cmd);
					message += String.format("!%s - %s%n", cmdObj.getName(), cmdObj.getDescription());
				}
			}
			
		} else {
			for (String cmdName : args) {
				if (server.cmds.validateCommand(cmdName)) {
					Command cmd = server.cmds.getCommandObj(cmdName);
					message += String.format("!%s - %s. Usage: %s%n", cmd.getName(), cmd.getDescription(), cmd.help());
				}
			}
		}

		return Utils.createMessage(String.format("```%s```", message));
	}
}
