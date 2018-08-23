package core.commands;

import core.Constants;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdBan extends Command {

	public CmdBan(Server server) {
		this.name = Constants.BAN_NAME;
		this.description = Constants.BAN_DESC;
		this.helpMsg = Constants.BAN_HELP;
		this.adminRequired = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {

		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		String username = args[0];
		Member m = server.getMember(username);

		if (server.isAdmin(m)) {
			throw new InvalidUseException("Cannot ban an admin");
		}

		server.banUser(m.getUser().getIdLong());

		this.response = Utils.createMessage(String.format("`%s banned`", m.getEffectiveName()));

		return response;
	}
}
