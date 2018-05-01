package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRemoveQueue extends Command {

	public CmdRemoveQueue() {
		this.helpMsg = Constants.REMOVEQUEUE_HELP;
		this.description = Constants.REMOVEQUEUE_DESC;
		this.name = Constants.REMOVEQUEUE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (args.length > 0) {
			for (String a : args) {
				try {
					qm.removeQueue(Integer.valueOf(a));
				} catch (NumberFormatException ex) {
					qm.removeQueue(a);
				}
			}
		} else {
			throw new BadArgumentsException();
		}
		qm.updateTopic();
		this.response = Utils.createMessage(String.format("Queue %s removed", args[0]), qm.getHeader(), true);
		System.out.println(success());
		
		return response;
	}
}
