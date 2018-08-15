package core.commands;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDeleteNotification extends Command {

	public CmdDeleteNotification(Server server) {
		this.helpMsg = Constants.DELETENOTIFICATION_HELP;
		this.description = Constants.DELETENOTIFICATION_DESC;
		this.name = Constants.DELETENOTIFICATION_NAME;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length > 1) {
			throw new BadArgumentsException();
		}

		if (args.length == 0) {
			for (Queue queue : qm.getQueueList()) {
				queue.removeNotification(caller.getUser());
			}

			this.response = Utils.createMessage("All notifications removed", "", true);

		} else {
			Queue queue;

			try {
				queue = qm.getQueue(Integer.valueOf(args[0]));
			} catch (NumberFormatException ex) {
				queue = qm.getQueue(args[0]);
			}

			if (queue == null) {
				throw new InvalidUseException("Queue does not exist");
			}
			queue.removeNotification(caller.getUser());

			this.response = Utils.createMessage(String.format("Notification in queue '%s' removed", queue.getName()),
					"", true);
		}

		return response;
	}
}
