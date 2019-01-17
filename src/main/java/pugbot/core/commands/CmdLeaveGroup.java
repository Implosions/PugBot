package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import pugbot.Utils;
import pugbot.core.exceptions.BadArgumentsException;

public class CmdLeaveGroup extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if(args.length == 0){
			throw new BadArgumentsException("No group specified");
		}
		
		String groupName = String.join(" ", args);
		Role role = server.getGroup(groupName);
		
		server.getGuild().getController().removeSingleRoleFromMember(caller, role).queue();
		
		return Utils.createMessage(String.format("`%s left the group: %s`", caller.getEffectiveName(), groupName));
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "LeaveGroup";
	}

	@Override
	public String getDescription() {
		return "Leaves a group and removes the associated role";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <group name>";
	}

}
