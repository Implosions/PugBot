package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdFinish extends Command {

	public CmdFinish(Server server) {
		this.helpMsg = Constants.FINISH_HELP;
		this.description = Constants.FINISH_DESC;
		this.name = Constants.FINISH_NAME;
		this.pugChannelOnlyCommand = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		
		response = Utils.createMessage(
				  "Deprecated command",
				  "This command has been deprecated, use `finishgame` instead\n"
				+ "See `!help finishgame` for more information", false);

		return response;
	}
}
