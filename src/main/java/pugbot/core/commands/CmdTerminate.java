package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.core.Constants;
import pugbot.core.exceptions.InvalidUseException;

public class CmdTerminate extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (!caller.getUser().getId().equals(Constants.OWNER_ID)) {
			throw new InvalidUseException("You do not have the required permissions");
		}

		System.exit(0);

		return null;
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
		return "Terminate";
	}

	@Override
	public String getDescription() {
		return "Shuts down the bot";
	}

	@Override
	public String getHelp() {
		return getBaseCommand();
	}
}
