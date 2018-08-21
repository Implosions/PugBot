package core.commands;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdAdd extends Command {

	public CmdAdd(Server server) {
		this.helpMsg = Constants.ADD_HELP;
		this.description = Constants.ADD_DESC;
		this.name = Constants.ADD_NAME;
		this.pugChannelOnlyCommand = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (qm.isQueueListEmpty()) {
			throw new InvalidUseException("There are no active queues");
		}

		if (qm.isPlayerIngame(caller)) {
			throw new InvalidUseException("You are already in-game");
		}

		String queueMsg;

		if (args.length == 0) {
			for (Queue queue : qm.getQueueList()) {
				queue.add(caller);
			}

			queueMsg = caller.getEffectiveName() + " added to all queues";
		} else {
			String queueNames = "";
			for (Queue queue : qm.getListOfQueuesFromStringArgs(args)) {
				queue.add(caller);
				queueNames += queue.getName() + ", ";
			}

			if (queueNames.isEmpty()) {
				throw new InvalidUseException("No valid queues named");
			}

			queueNames = queueNames.substring(0, queueNames.length() - 2);
			queueMsg = caller.getEffectiveName() + " added to " + queueNames;
		}

		qm.updateTopic();

		if (qm.hasPlayerJustFinished(caller)) {
			this.response = Utils.createMessage(queueMsg,
					String.format("Your game has just finished, you will be randomized into the queue after %d seconds",
							server.getSettingsManager().getQueueFinishTimer()),
					true);
		} else {
			this.response = Utils.createMessage(queueMsg, qm.getHeader(), true);
		}

		return response;
	}
}
