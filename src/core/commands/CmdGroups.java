package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdGroups extends Command{

	public CmdGroups(Server server){
		this.name = Constants.GROUPS_NAME;
		this.description = Constants.GROUPS_DESC;
		this.helpMsg = Constants.GROUPS_HELP;
		this.server = server;
	}
	
	@Override
	public Message execCommand(Member caller, String[] args) {
		String formattedNames = String.join(", ", server.getGroupNames());
		
		if(formattedNames.isEmpty()){
			formattedNames = " ";
		}
		
		response = Utils.createMessage("Available groups:", formattedNames, true);
		
		return response;
	}

}
