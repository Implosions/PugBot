package core.commands;

import core.entities.Queue;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdNotify extends Command {

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
			queue = qm.getQueueByIndex(Integer.valueOf(args[0]));
		} catch (NumberFormatException ex) {
			queue = qm.getQueueByName(args[0]);
		}

		if (queue == null) {
			throw new InvalidUseException("Queue does not exist");
		}

		if (playerCount < 1 || playerCount >= queue.getMaxPlayers()) {
			throw new InvalidUseException("Player count must be greater than zero and less than the maximum amount of players");
		}

		queue.addNotification(caller, playerCount);
		return Utils.createMessage(
				String.format("`Notification added to queue '%s' at %d players`", queue.getName(), playerCount));
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
		return "Notify";
	}

	@Override
	public String getDescription() {
		return "Adds a notification to a game queue so that you will be messaged when a specified player count is reached";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " <queue name> <player count>\n" +
				getBaseCommand() + " <queue index> <player count>";
	}
}
