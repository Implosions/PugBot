package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdGithub extends Command {

	public CmdGithub(Server server) {
		this.name = Constants.GITHUB_NAME;
		this.description = Constants.GITHUB_DESC;
		this.helpMsg = Constants.GITHUB_HELP;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		return Utils.createMessage("https://github.com/Implosions/PugBot");
	}
}
