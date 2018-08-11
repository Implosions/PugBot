package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDeleteGroup extends Command{

	public CmdDeleteGroup(){
		this.name = Constants.DELETEGROUP_NAME;
		this.description = Constants.DELETEGROUP_DESC;
		this.helpMsg = Constants.DELETEGROUP_HELP;
		this.adminRequired = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length == 0){
			throw new BadArgumentsException("Must specify at least one group name");
		}
		
		String groupName = String.join(" ", args);
		server.deleteGroup(groupName);
		
		response = Utils.createMessage(String.format("`Group '%s' deleted`", groupName));
		
		return response;
	}

}
