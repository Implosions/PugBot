package core.commands;

import core.Constants;
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
		this.helpMsg = Constants.CREATEQUEUE_HELP;
		this.description = Constants.CREATEQUEUE_DESC;
		this.name = Constants.CREATEQUEUE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
		this.server = server;
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

		int queueId = Database.insertQueue(server.getId(), queueName, playerCount);
		this.response = Utils.createMessage(String.format("`Queue %s created`", queueName));

		qm.addQueue(new Queue(queueName, playerCount, server.getId(), queueId));
		qm.updateTopic();

		return response;
	}
}