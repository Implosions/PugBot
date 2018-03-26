package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdAddAdmin extends Command{

	public CmdAddAdmin(){
		this.name = Constants.ADDADMIN_NAME;
		this.description = Constants.ADDADMIN_DESC;
		this.helpMsg = Constants.ADDADMIN_HELP;
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
				server.addAdmin(m.getUser().getId());
			} else {
				throw new DoesNotExistException("User");
			}
		} else {
			throw new BadArgumentsException();
		}
		this.response = Utils.createMessage(String.format("`%s is now an admin`", pName));
		System.out.println(success());
	}

}
