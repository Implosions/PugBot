package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdUnban extends Command{

	public CmdUnban(){
		this.name = Constants.UNBAN_NAME;
		this.description = Constants.UNBAN_DESC;
		this.helpMsg = Constants.UNBAN_HELP;
		this.adminRequired = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		String pName = "All";
		if (args.length == 0) {
			server.unbanAll();
		} else if (args.length == 1) {
			Member m = server.getMember(args[0]);
			if (m != null) {
				pName = m.getEffectiveName();
				server.unbanUser(m.getUser().getId());
			} else {
				throw new DoesNotExistException("User");
			}
		} else {
			throw new BadArgumentsException();
		}
		this.response = Utils.createMessage(String.format("`%s unbanned`", pName));
		System.out.println(success());
		
		return response;
	}
}
