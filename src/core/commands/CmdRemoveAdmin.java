package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRemoveAdmin extends Command {

	public CmdRemoveAdmin(Server server) {
		this.name = Constants.REMOVEADMIN_NAME;
		this.description = Constants.REMOVEADMIN_DESC;
		this.helpMsg = Constants.REMOVEADMIN_HELP;
		this.adminRequired = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		Member m = server.getMember(args[0]);

		if (server.isAdmin(m)) {
			server.removeAdmin(m.getUser().getIdLong());
		} else {
			throw new InvalidUseException(m.getEffectiveName() + " is not an admin");
		}

		this.response = Utils.createMessage(String.format("`%s's admin removed`", m.getEffectiveName()));

		return response;
	}

}
