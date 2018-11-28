package core.commands;

import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdBan extends Command {

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

		return Utils.createMessage(String.format("`%s banned`", m.getEffectiveName()));
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
		return "Ban";
	}

	@Override
	public String getDescription() {
		return "Bans a user from interacting with the bot";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <user>";
	}
}
