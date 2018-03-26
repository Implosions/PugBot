package core.commands;

import java.util.Arrays;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdRemove extends Command {

	public CmdRemove() {
		this.helpMsg = Constants.REMOVE_HELP;
		this.description = Constants.REMOVE_DESC;
		this.name = Constants.REMOVE_NAME;
		this.adminRequired = true;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		String pName;
		if (args.length > 0) {
			Member m = server.getMember(args[0]);

			if (m != null) {
				pName = m.getEffectiveName();
				if (args.length == 1) {
					qm.remove(m.getUser());
				} else {
					for (String s : Arrays.copyOfRange(args, 1, args.length)) {
						try {
							qm.remove(m.getUser(), Integer.valueOf(s));
						} catch (NumberFormatException ex) {
							qm.remove(m.getUser(), s);
						}
					}
				}
			} else {
				throw new BadArgumentsException(args[0] + " does not exist");
			}
		} else {
			throw new BadArgumentsException();
		}
		qm.updateTopic();
		this.response = Utils.createMessage(String.format("%s removed from queue", pName), qm.getHeader(), true);
		System.out.println(success());
	}

}
