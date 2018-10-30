package core.commands;

import core.Database;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdCreateQueue extends Command {

	public CmdCreateQueue(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length != 2) {
			throw new BadArgumentsException();
		}

		String queueName = args[0];
		int playerCount;

		if (qm.doesQueueExist(queueName)) {
			throw new InvalidUseException("A queue with that name already exists");
		}

		try {
			playerCount = Integer.valueOf(args[1]);
		} catch (NumberFormatException ex) {
			throw new BadArgumentsException("Max players must be an integer value");
		}

		if (playerCount < 2) {
			throw new InvalidUseException("Max players must be greater than 1");
		}

		long timestamp = System.currentTimeMillis();
		
		Database.insertQueue(server.getId(), timestamp, queueName, playerCount);

		qm.addQueue(new Queue(queueName, playerCount, timestamp, qm));
		qm.updateTopic();

		return Utils.createMessage(String.format("`Queue %s created`", queueName));
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
		return "CreateQueue";
	}

	@Override
	public String getDescription() {
		return "Creates a new game queue that users can join and leave from";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <queue name> <queue size>";
	}
}