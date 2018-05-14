package core.commands;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDel extends Command {

	public CmdDel() {
		this.helpMsg = Constants.DEL_HELP;
		this.description = Constants.DEL_DESC;
		this.name = Constants.DEL_NAME;
		this.pugChannelOnlyCommand = true;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (qm.isPlayerInQueue(member.getUser()) || qm.isPlayerWaiting(member.getUser())) {
			if (args.length == 0) {
				for(Queue queue : qm.getQueueList()){
					queue.delete(member.getUser());
				}
				
				this.response = Utils.createMessage(String.format("%s deleted from all queues", member.getEffectiveName()),
						qm.getHeader(), true);
			} else {
				String queueNames = "";
				for (String arg : args) {
					Queue queue;
					
					try {
						queue = qm.getQueue(Integer.valueOf(arg));
					} catch (NumberFormatException ex) {
						queue = qm.getQueue(arg);
					}
					
					if(queue != null){
						queue.delete(member.getUser());
						queueNames += queue.getName() + ", ";
					}
				}
				
				queueNames = queueNames.substring(0, queueNames.length() - 2);
				this.response = Utils.createMessage(String.format("%s deleted from %s", member.getEffectiveName(), queueNames),
						qm.getHeader(), true);
			}
			qm.updateTopic();
		} else {
			throw new InvalidUseException("You are not in any queue");
		}
		System.out.println(success());
		
		return response;
	}
}
