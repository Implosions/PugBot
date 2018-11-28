package core.commands;

import core.entities.Queue;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDeleteNotification extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length > 1) {
			throw new BadArgumentsException();
		}

		if (args.length == 0) {
			for (Queue queue : qm.getQueueList()) {
				queue.removeNotification(caller);
			}

			return Utils.createMessage("All notifications removed", "", true);

		} else {
			Queue queue;

			try {
				queue = qm.getQueueByIndex(Integer.valueOf(args[0]));
			} catch (NumberFormatException ex) {
				queue = qm.getQueueByName(args[0]);
			}

			if (queue == null) {
				throw new InvalidUseException("Queue does not exist");
			}
			
			queue.removeNotification(caller);

			return Utils.createMessage(String.format("Notification in queue '%s' removed", queue.getName()),
					"", true);
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
		return "DeleteNotification";
	}

	@Override
	public String getDescription() {
		return "Deletes queue notifications";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Deletes all notifications\n" +
				getBaseCommand() + " <queue name|queue index> - Deletes all notifications for a specific queue";
	}
}
