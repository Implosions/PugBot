package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdCreateGroup extends Command{
	
	public CmdCreateGroup(){
		this.name = Constants.ADDGROUP_NAME;
		this.description = Constants.ADDGROUP_DESC;
		this.helpMsg = Constants.ADDGROUP_HELP;
		this.adminRequired = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length == 0){
			throw new BadArgumentsException("Must specify at least one group name");
		}
		
		String groupName = String.join(" ", args);
		server.addGroup(groupName);
		
		response = Utils.createMessage(String.format("`Group '%s' created`", groupName));
		
		return response;
	}

}
