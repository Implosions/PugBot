package core.commands;

import core.Constants;
import core.entities.Server;
import core.entities.ServerManager;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRestart extends Command{

	public CmdRestart(){
		this.name = Constants.RESTART_NAME;
		this.description = Constants.RESTART_DESC;
		this.helpMsg = Constants.RESTART_HELP;
		this.adminRequired = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		server.getQueueManager().finishAllGames();
		ServerManager.removeServer(server.getid());
		ServerManager.addNewServer(server.getGuild());
		
		this.response = Utils.createMessage("`Server instance restarted`");
		System.out.println(success());
		
		return response;
	}

}
