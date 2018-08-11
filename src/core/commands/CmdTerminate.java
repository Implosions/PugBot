package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdTerminate extends Command{
	public CmdTerminate(){
		this.name = Constants.TERMINATE_NAME;
		this.helpMsg = Constants.TERMINATE_HELP;
		this.description = Constants.TERMINATE_DESC;
	}
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if (member.getUser().getId().equals(Constants.OWNER_ID)) {
			System.out.println(success());
			System.exit(0);
		} else {
			throw new InvalidUseException("You do not have the required permissions");
		}
		
		return null;
	}
}
