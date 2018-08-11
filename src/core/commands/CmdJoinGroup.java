package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class CmdJoinGroup extends Command{

	public CmdJoinGroup(){
		this.name = Constants.JOINGROUP_NAME;
		this.description = Constants.JOINGROUP_DESC;
		this.helpMsg = Constants.JOINGROUP_HELP;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length == 0){
			throw new BadArgumentsException("No group specified");
		}
		
		String groupName = String.join(" ", args);
		Role role = server.getGroup(groupName);
		
		server.getGuild().getController().addSingleRoleToMember(member, role).queue();
		
		response = Utils.createMessage(String.format("`%s joined the group: %s`", member.getEffectiveName(), groupName));
		
		return response;
	}

}
