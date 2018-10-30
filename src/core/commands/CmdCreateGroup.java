package core.commands;

import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdCreateGroup extends Command {

	public CmdCreateGroup(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length == 0) {
			throw new BadArgumentsException("Must specify at least one group name");
		}

		String groupName = String.join(" ", args);
		server.addGroup(groupName);

		return Utils.createMessage(String.format("`Group '%s' created`", groupName));
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "CreateGroup";
	}

	@Override
	public String getDescription() {
		return "Creates a pingable role that users can join and leave from";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <group name>";
	}
}
