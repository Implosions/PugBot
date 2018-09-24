package core.commands;

import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdAddAdmin extends Command {

	public CmdAddAdmin(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		String username = args[0];
		Member m = server.getMember(username);

		server.addAdmin(m.getUser().getIdLong());

		this.response = Utils.createMessage(String.format("`%s is now an admin`", m.getEffectiveName()));

		return response;
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return true;
	}

	@Override
	public String getName() {
		return "AddAdmin";
	}

	@Override
	public String getDescription() {
		return "Gives a user bot admin privileges";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <username>";
	}
}
