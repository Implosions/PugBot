package core.commands;

import java.util.Arrays;

import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRemove extends Command {

	public CmdRemove(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length == 0) {
			throw new BadArgumentsException();
		}

		Member m = server.getMember(args[0]);

		if (args.length == 1) {
			qm.purgeQueue(m);
			qm.updateTopic();
			
			return Utils.createMessage(String.format("%s removed from all queues", m.getEffectiveName()),
					qm.getHeader(), true);
		}
		else {
			String queueNames = "";
			
			for (Queue queue : qm.getListOfQueuesFromStringArgs(Arrays.copyOfRange(args, 1, args.length))) {
				queue.delete(m);
				queueNames += queue.getName() + ", ";
			}

			if (queueNames.isEmpty()) {
				throw new InvalidUseException("No valid queue named");
			}

			queueNames = queueNames.substring(0, queueNames.length() - 2);
			qm.updateTopic();
			
			return Utils.createMessage(String.format("%s removed from %s", m.getEffectiveName(), queueNames),
					qm.getHeader(), true);
		}
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
		return "Remove";
	}

	@Override
	public String getDescription() {
		return "Removes a player from a queue";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " <username> - Removes a player from all queues\n" +
				getBaseCommand() + " <username> <queue name|queue index> - Removes a player from a specific queue";
	}

}
