package core.commands;

import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDeleteGroup extends Command{

	@Override
	public Message execCommand(Member caller, String[] args) {
		if(args.length == 0){
			throw new BadArgumentsException("Must specify at least one group name");
		}
		
		String groupName = String.join(" ", args);
		server.deleteGroup(groupName);
		
		return Utils.createMessage(String.format("`Group '%s' deleted`", groupName));
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "DeleteGroup";
	}

	@Override
	public String getDescription() {
		return "Deletes a group";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <group name>";
	}

}
