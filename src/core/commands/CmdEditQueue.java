package core.commands;

import core.Database;
import core.entities.Queue;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdEditQueue extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length != 3) {
			throw new BadArgumentsException();
		}

		int playerCount;
		String newName = args[1];
		Queue queue;

		try {
			playerCount = Integer.valueOf(args[2]);
		} catch (NumberFormatException ex) {
			throw new InvalidUseException("Player count must be a valid number");
		}

		try {
			queue = qm.getQueueByIndex(Integer.valueOf(args[0]));
		} catch (NumberFormatException ex) {
			queue = qm.getQueueByName(args[0]);
		}

		if (queue == null) {
			throw new InvalidUseException("Queue does not exist");
		}

		if (queue.getCurrentPlayersCount() >= playerCount) {
			throw new InvalidUseException("New player count must be greater than the amount of current players");
		}

		queue.setName(newName);
		queue.setMaxPlayers(playerCount);

		Database.updateQueue(server.getId(), queue.getId(), queue.getName(), queue.getMaxPlayers());
		qm.updateTopic();

		return Utils.createMessage(String.format("`Queue %s edited`", queue.getName()));
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
		return "EditQueue";
	}

	@Override
	public String getDescription() {
		return "Edits an active queue's name and max player count";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " <queue name> <new queue name> <new max players>\n" +
				getBaseCommand() + " <queue index> <new queue name> <new max players>";
	}
}
