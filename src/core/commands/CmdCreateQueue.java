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

	public CmdCreateQueue() {
		this.helpMsg = Constants.CREATEQUEUE_HELP;
		this.description = Constants.CREATEQUEUE_DESC;
		this.name = Constants.CREATEQUEUE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (args.length == 2) {
			try {
				String queueName = args[0];
				int playerCount = Integer.valueOf(args[1]);
				
				if(playerCount > 1){
					if(!qm.doesQueueExist(queueName)){
						int queueId = Database.insertQueue(server.getid(), queueName, playerCount);
						qm.addQueue(new Queue(queueName, playerCount, server.getid(), queueId));
						
						this.response = Utils.createMessage(String.format("`Queue %s created`", queueName));
						qm.updateTopic();
					}else{
						throw new InvalidUseException("A queue with that name already exists");
					}
					
				}else{
					throw new InvalidUseException("Max players must be greater than 1");
				}
				
			} catch (NumberFormatException ex) {
				throw new BadArgumentsException("Max players must be an integer value");
			}
			
		} else {
			throw new BadArgumentsException();
		}
		System.out.println(success());
		
		return response;
	}
}