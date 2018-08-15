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

public class CmdNotify extends Command {

	public CmdNotify(Server server) {
		this.helpMsg = Constants.NOTIFY_HELP;
		this.description = Constants.NOTIFY_DESC;
		this.name = Constants.NOTIFY_NAME;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length != 2) {
			throw new BadArgumentsException();
		}

		Integer playerCount;
		Queue queue;

		try {
			playerCount = Integer.valueOf(args[1]);
		} catch (NumberFormatException ex) {
			throw new InvalidUseException("Player count must be a valid integer");
		}

		try {
			queue = qm.getQueue(Integer.valueOf(args[0]));
		} catch (NumberFormatException ex) {
			queue = qm.getQueue(args[0]);
		}

		if (queue == null) {
			throw new InvalidUseException("Queue does not exist");
		}

		if (playerCount < 1 && playerCount >= queue.getMaxPlayers()) {
			throw new InvalidUseException("Player count must be greater than zero and less than the maximum amount of players");
		}

		queue.addNotification(caller.getUser(), playerCount);
		this.response = Utils.createMessage(
				String.format("`Notification added to queue '%s' at %d players`", queue.getName(), playerCount));

		return response;
	}
}
