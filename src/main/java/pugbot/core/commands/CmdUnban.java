package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.exceptions.BadArgumentsException;

public class CmdUnban extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		String pName = "All";

		if (args.length < 1) {
			throw new BadArgumentsException();
		}

		if (args.length == 0) {
			server.unbanAll();
		} else if (args.length == 1) {
			Member m = server.getMember(args[0]);
			pName = m.getEffectiveName();

			server.unbanUser(m.getUser().getIdLong());
		}

		return Utils.createMessage(String.format("`%s has been unbanned`", pName));
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
		return "Unban";
	}

	@Override
	public String getDescription() {
		return "Unbans a user";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <username>";
	}
}
