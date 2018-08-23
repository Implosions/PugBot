package core.commands;

import java.util.List;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdAdd extends Command {

	public CmdAdd(Server server) {
		this.helpMsg = Constants.ADD_HELP;
		this.description = Constants.ADD_DESC;
		this.name = Constants.ADD_NAME;
		this.pugChannelOnlyCommand = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (qm.isQueueListEmpty()) {
			throw new InvalidUseException("There are no active queues");
		}

		if (qm.isPlayerIngame(caller)) {
			throw new InvalidUseException("You are already in-game");
		}

		List<Queue> queueList;
		
		if (args.length == 0) {
			queueList = qm.getQueueList();
		} else {
			queueList = qm.getListOfQueuesFromStringArgs(args);

			if (queueList.isEmpty()) {
				throw new InvalidUseException("No valid queues named");
			}
		}
		
		boolean justFinished = qm.hasPlayerJustFinished(caller);
		StringBuilder stringBuilder = new StringBuilder(caller.getEffectiveName() + " added to: ");
		
		for(Queue queue : queueList){
			stringBuilder.append(queue.getName() + ", ");
			
			if(justFinished){
				queue.addToWaitList(caller);
			}else{
				queue.addToQueue(caller);
			}
		}
		
		stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
		qm.updateTopic();

		if (justFinished) {
			String s = String.format("Your game has just finished, you will be randomized into the queue in %d seconds",
					qm.getWaitTimeRemaining(caller));
			
			this.response = Utils.createMessage(stringBuilder.toString(), s, true);
		} else {
			this.response = Utils.createMessage(stringBuilder.toString(), qm.getHeader(), true);
		}

		return response;
	}
}
