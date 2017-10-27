package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DuplicateEntryException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdCreateQueue extends Command {

	public CmdCreateQueue() {
		this.helpMsg = Constants.CREATEQUEUE_HELP;
		this.description = Constants.CREATEQUEUE_DESC;
		this.name = Constants.CREATEQUEUE_NAME;
		this.adminRequired = true;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try {
			if (args.length == 2) {
				try{
					qm.createQueue(args[0], Integer.valueOf(args[1]));
				}catch(NumberFormatException ex){
					throw new BadArgumentsException("Max players must be an integer value");
				}
			} else {
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Functions.createMessage(String.format("Queue %s created", args[0]), qm.getHeader(), true);
			System.out.println(successMsg);
		} catch (BadArgumentsException | DuplicateEntryException ex) {
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}
}
