package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;

public class CmdTerminate extends Command{
	public CmdTerminate(){
		this.name = Constants.TERMINATE_NAME;
		this.helpMsg = Constants.TERMINATE_HELP;
		this.description = Constants.TERMINATE_DESC;
	}
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		if (member.getUser().getId().equals(Constants.OWNER_ID)) {
			System.out.println("getSuccess()");
			System.exit(0);
		} else {
			throw new InvalidUseException("You do not have the required permissions");
		}
	}
}
