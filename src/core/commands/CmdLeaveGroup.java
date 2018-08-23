package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class CmdLeaveGroup extends Command{

	public CmdLeaveGroup(Server server){
		this.name = Constants.LEAVEGROUP_NAME;
		this.description = Constants.LEAVEGROUP_DESC;
		this.helpMsg = Constants.LEAVEGROUP_HELP;
		this.server = server;
	}
	
	@Override
	public Message execCommand(Member caller, String[] args) {
		if(args.length == 0){
			throw new BadArgumentsException("No group specified");
		}
		
		String groupName = String.join(" ", args);
		Role role = server.getGroup(groupName);
		
		server.getGuild().getController().removeSingleRoleFromMember(caller, role).queue();
		
		response = Utils.createMessage(String.format("`%s left the group: %s`", caller.getEffectiveName(), groupName));
		
		return response;
	}

}
