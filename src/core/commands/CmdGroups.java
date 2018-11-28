package core.commands;

import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdGroups extends Command{

	@Override
	public Message execCommand(Member caller, String[] args) {
		String formattedNames = String.join(", ", server.getGroupNames());
		
		if(formattedNames.isEmpty()){
			formattedNames = " ";
		}
		
		return Utils.createMessage("Available groups:", formattedNames, true);
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
		return "Groups";
	}

	@Override
	public String getDescription() {
		return "Lists the currenty available groups";
	}

	@Override
	public String getHelp() {
		return getBaseCommand();
	}

}
