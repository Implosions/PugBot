package core.commands;

import core.Constants;
import core.entities.Server;
import net.dv8tion.jda.core.entities.Member;

public class CmdTerminate extends Command{
	public CmdTerminate(){
		this.name = Constants.TERMINATE_NAME;
		this.helpMsg = Constants.TERMINATE_HELP;
		this.description = Constants.TERMINATE_DESC;
		this.adminRequired = true;
	}
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		System.out.println("Terminating bot...");
		System.exit(0);
	}

}
