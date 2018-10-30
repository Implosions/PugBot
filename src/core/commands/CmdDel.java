package core.commands;

import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDel extends Command {

	public CmdDel(Server server) {
		super(server);
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

			return Utils.createMessage(String.format("%s deleted from all queues", caller.getEffectiveName()),
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
			
			qm.updateTopic();
			queueNames = queueNames.substring(0, queueNames.length() - 2);
			
			return Utils.createMessage(
					String.format("%s deleted from %s", caller.getEffectiveName(), queueNames), qm.getHeader(), true);
		}
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "Del";
	}

	@Override
	public String getDescription() {
		return "Leaves a game queue";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Removes yourself from all queues\n" +
				getBaseCommand() + " <queue name>... - Removes yourself from all queues named\n" +
				getBaseCommand() + " <index>... - Removes yourself from all queues at the specified indices";
	}
}
