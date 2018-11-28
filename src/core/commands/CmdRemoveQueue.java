package core.commands;

import core.Database;
import core.entities.Queue;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRemoveQueue extends Command {

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
		
		return Utils.createMessage(String.format("`Queue: %s removed`", queueNames));
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
		return "RemoveQueue";
	}

	@Override
	public String getDescription() {
		return "Removes a queue from the queue list";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " <queue name>\n" + 
				getBaseCommand() + " <queue index>";
	}
}
