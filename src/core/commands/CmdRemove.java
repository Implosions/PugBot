package core.commands;

import java.util.Arrays;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRemove extends Command {

	public CmdRemove() {
		this.helpMsg = Constants.REMOVE_HELP;
		this.description = Constants.REMOVE_DESC;
		this.name = Constants.REMOVE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		String pName;
		if (args.length > 0) {
			Member m = server.getMember(args[0]);

			if (m != null) {
				pName = m.getEffectiveName();
				if (args.length == 1) {
					for(Queue queue : qm.getQueueList()){
						queue.delete(m.getUser());
					}
				} else {
					for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
						Queue queue;
						
						try {
							queue = qm.getQueue(Integer.valueOf(arg));
						} catch (NumberFormatException ex) {
							queue = qm.getQueue(arg);
						}
						
						if(queue != null){
							queue.delete(m.getUser());
						}else{
							throw new InvalidUseException(String.format("Queue '%s' does not exist", arg));
						}
					}
				}
			} else {
				throw new InvalidUseException(String.format("Could not find user '%s'", args[0]));
			}
		} else {
			throw new BadArgumentsException();
		}
		qm.updateTopic();
		this.response = Utils.createMessage(String.format("%s removed", pName), qm.getHeader(), true);
		System.out.println(success());
		
		return response;
	}

}
