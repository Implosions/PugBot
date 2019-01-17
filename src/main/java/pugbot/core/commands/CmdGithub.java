package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;

public class CmdGithub extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		return Utils.createMessage("https://github.com/Implosions/PugBot");
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return true;
	}

	@Override
	public String getName() {
		return "Github";
	}

	@Override
	public String getDescription() {
		return "Links this bot's github repository";
	}

	@Override
	public String getHelp() {
		return getBaseCommand();
	}
}
