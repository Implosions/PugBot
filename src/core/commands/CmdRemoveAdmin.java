package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdRemoveAdmin extends Command{

	public CmdRemoveAdmin(){
		this.name = Constants.REMOVEADMIN_NAME;
		this.description = Constants.REMOVEADMIN_DESC;
		this.helpMsg = Constants.REMOVEADMIN_HELP;
		this.adminRequired = true;
		this.pugCommand = false;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		String pName;
		if (args.length == 1) {
			Member m = server.getMember(args[0]);
			if (m != null) {
				pName = m.getEffectiveName();
				if (server.isAdmin(m)) {
					server.removeAdmin(m.getUser().getId());
				} else {
					throw new InvalidUseException(pName + " is not an admin");
				}
			} else {
				throw new DoesNotExistException("User");
			}
		} else {
			throw new BadArgumentsException();
		}
		this.response = Utils.createMessage(String.format("`%s's admin removed`", pName));
		System.out.println(success());
	}

}
