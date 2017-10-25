package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
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
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try {
			if (args.size() == 2) {
				try{
					qm.create(args.get(0), Integer.valueOf(args.get(1)));
				}catch(NumberFormatException ex){
					throw new BadArgumentsException("Max players must be an integer value");
				}
			} else {
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Functions.createMessage(String.format("Queue %s created", args.get(0)), qm.getHeader(), true);
			System.out.println(successMsg);
		} catch (BadArgumentsException | DuplicateEntryException ex) {
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}
}
