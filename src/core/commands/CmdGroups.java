package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdGroups extends Command{

	public CmdGroups(){
		this.name = Constants.DELETEGROUP_NAME;
		this.description = Constants.DELETEGROUP_DESC;
		this.helpMsg = Constants.DELETEGROUP_HELP;
		this.pugChannelOnlyCommand = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		String formattedNames = String.join(", ", server.getGroupNames());
		
		if(formattedNames.isEmpty()){
			formattedNames = " ";
		}
		
		response = Utils.createMessage("Available groups:", formattedNames, true);
		
		return response;
	}

}