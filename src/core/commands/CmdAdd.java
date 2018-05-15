package core.commands;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class CmdAdd extends Command{
	
	public CmdAdd(){
		this.helpMsg = Constants.ADD_HELP;
		this.description = Constants.ADD_DESC;
		this.name = Constants.ADD_NAME;
		this.pugChannelOnlyCommand = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		User player = member.getUser();
		if(!qm.isQueueListEmpty()){
			if(!qm.isPlayerIngame(player)){
				String queueMsg;
				
				if (args.length == 0) {
					for(Queue queue : qm.getQueueList()){
						queue.add(member.getUser());
					}
					queueMsg = member.getEffectiveName() + " added to all queues";
				} else {
					String queueNames = "";
					for (Queue queue : qm.getListOfQueuesFromStringArgs(args)) {
						queue.add(member.getUser());
						queueNames += queue.getName() + ", ";
					}
					
					if(queueNames.isEmpty()){
						throw new InvalidUseException("No valid queues named");
					}
					
					queueNames = queueNames.substring(0, queueNames.length() - 2);
					queueMsg = member.getEffectiveName() + " added to " + queueNames;
				}
				qm.updateTopic();
				if (qm.hasPlayerJustFinished(member.getUser())) {
					this.response = Utils.createMessage(queueMsg,
							String.format("Your game has just finished, you will be randomized into the queue after %d seconds",
									server.getSettings().getQueueFinishTimer()), true);
				} else {
					this.response = Utils.createMessage(queueMsg, qm.getHeader(), true);
				}
				
			}else{
				throw new InvalidUseException("You are already in-game");
			}
			
		}else{
			throw new InvalidUseException("There are no active queues");
		}
		System.out.println(success());
		return response;
	}
}
