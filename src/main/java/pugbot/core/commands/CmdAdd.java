package pugbot.core.commands;

import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.Queue;
import pugbot.core.entities.QueueManager;
import pugbot.core.exceptions.InvalidUseException;

public class CmdAdd extends Command {

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
			
			if(qm.isPlayerIngame(caller)){
				break;
			}
		}
		
		stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
		qm.updateTopic();

		if (justFinished) {
			String s = String.format("Your game has just finished, you will be randomized into the queue in %d seconds",
					qm.getWaitTimeRemaining(caller));
			
			return Utils.createMessage(stringBuilder.toString(), s, true);
		} 
		else if (qm.isPlayerIngame(caller)){
			return null;
		}
		else {
			return Utils.createMessage(stringBuilder.toString(), qm.getHeader(), true);
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
