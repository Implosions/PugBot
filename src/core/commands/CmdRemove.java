package core.commands;

import java.util.Arrays;

import core.Constants;
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
		this.helpMsg = Constants.REMOVE_HELP;
		this.description = Constants.REMOVE_DESC;
		this.name = Constants.REMOVE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
		this.server = server;
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
			this.response = Utils.createMessage(String.format("%s removed from all queues", m.getEffectiveName()),
					qm.getHeader(), true);
		} else {
			String queueNames = "";
			
			for (Queue queue : qm.getListOfQueuesFromStringArgs(Arrays.copyOfRange(args, 1, args.length))) {
				queue.delete(m);
				queueNames += queue.getName() + ", ";
			}

			if (queueNames.isEmpty()) {
				throw new InvalidUseException("No valid queue named");
			}

			queueNames = queueNames.substring(0, queueNames.length() - 2);

			this.response = Utils.createMessage(String.format("%s removed from %s", m.getEffectiveName(), queueNames),
					qm.getHeader(), true);
		}

		qm.updateTopic();

		return response;
	}

}
