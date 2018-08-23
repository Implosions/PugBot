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

public class CmdRemoveQueue extends Command {

	public CmdRemoveQueue(Server server) {
		this.helpMsg = Constants.REMOVEQUEUE_HELP;
		this.description = Constants.REMOVEQUEUE_DESC;
		this.name = Constants.REMOVEQUEUE_NAME;
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
		
		String queueNames = "";
		for (Queue queue : qm.getListOfQueuesFromStringArgs(args)) {
			qm.removeQueue(queue);
			Database.deactivateQueue(server.getId(), queue.getId());
			queueNames += queue.getName() + ", ";
		}
		
		if(queueNames.isEmpty()){
			throw new InvalidUseException("No valid queue named");
		}
		
		queueNames = queueNames.substring(0, queueNames.length() - 2);
		qm.updateTopic();
		this.response = Utils.createMessage(String.format("`Queue: %s removed`", queueNames));
		
		return response;
	}
}
