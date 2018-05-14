package core.commands;

import core.Constants;
import core.Database;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRemoveQueue extends Command {

	public CmdRemoveQueue() {
		this.helpMsg = Constants.REMOVEQUEUE_HELP;
		this.description = Constants.REMOVEQUEUE_DESC;
		this.name = Constants.REMOVEQUEUE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (args.length > 0) {
			for (String arg : args) {
				Queue queue;
				
				try {
					queue = qm.getQueue(Integer.valueOf(arg));
				} catch (NumberFormatException ex) {
					queue = qm.getQueue(arg);
				}
				
				if(queue != null){
					qm.removeQueue(queue);
					Database.deactivateQueue(server.getid(), queue.getId());
					qm.updateTopic();
				}
				
			}
			this.response = Utils.createMessage("`Queue(s) removed`");
		} else {
			throw new BadArgumentsException();
		}
		System.out.println(success());
		
		return response;
	}
}
