package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdMumble extends Command{
	
	public CmdMumble(){
		this.helpMsg = Constants.MUMBLE_HELP;
		this.description = Constants.MUMBLE_DESC;
		this.name = Constants.MUMBLE_NAME;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		this.response = Utils.createMessage("", server.getSettings().mumble(), true);
		System.out.println(success());
		
		return response;
	}

}
