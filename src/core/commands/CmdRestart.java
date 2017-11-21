package core.commands;

import core.Constants;
import core.entities.Server;
import core.entities.ServerManager;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdRestart extends Command{

	public CmdRestart(){
		this.name = Constants.RESTART_NAME;
		this.description = Constants.RESTART_DESC;
		this.helpMsg = Constants.RESTART_HELP;
		this.adminRequired = true;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		ServerManager.removeServer(server.getGuild());
		ServerManager.addNewServer(server.getGuild());
		this.response = Utils.createMessage("`Server instance restarted`");
	}

}
