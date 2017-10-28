package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdMumble extends Command{
	
	public CmdMumble(){
		this.helpMsg = Constants.MUMBLE_HELP;
		this.description = Constants.MUMBLE_DESC;
		this.name = Constants.MUMBLE_NAME;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		this.response = Utils.createMessage("", server.getSettings().mumble(), true);
	}

}
