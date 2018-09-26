package core.commands;

import java.util.List;

import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdAdd extends Command {

	public CmdAdd(Server server) {
		super(server);
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
			if(!queue.isPlayerEligible(caller)){
				continue;
			}
			
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
		return "Add";
	}

	@Override
	public String getDescription() {
		return "Adds yourself to a game queue";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Joins all available queues\n" +
				getBaseCommand() + " <queue name>... - Joins all queues named\n" +
				getBaseCommand() + " <index>... - Joins all queues at the specified indices";
	}
}
