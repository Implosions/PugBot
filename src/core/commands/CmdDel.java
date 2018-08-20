package core.commands;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDel extends Command {

	public CmdDel(Server server) {
		this.helpMsg = Constants.DEL_HELP;
		this.description = Constants.DEL_DESC;
		this.name = Constants.DEL_NAME;
		this.pugChannelOnlyCommand = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (!qm.isPlayerInQueue(caller) && !qm.isPlayerWaiting(caller)) {
			throw new InvalidUseException("You are not in any queue");
		}

		if (args.length == 0) {
			for (Queue queue : qm.getQueueList()) {
				queue.delete(caller);
			}

			this.response = Utils.createMessage(String.format("%s deleted from all queues", caller.getEffectiveName()),
					qm.getHeader(), true);
		} else {
			String queueNames = "";
			for (Queue queue : qm.getListOfQueuesFromStringArgs(args)) {
				queue.delete(caller);
				queueNames += queue.getName() + ", ";
			}

			if (queueNames.isEmpty()) {
				throw new InvalidUseException("No valid queues named");
			}

			queueNames = queueNames.substring(0, queueNames.length() - 2);
			this.response = Utils.createMessage(
					String.format("%s deleted from %s", caller.getEffectiveName(), queueNames), qm.getHeader(), true);
		}

		qm.updateTopic();

		return response;
	}
}
