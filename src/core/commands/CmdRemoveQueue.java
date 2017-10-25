package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdRemoveQueue extends Command {

	public CmdRemoveQueue() {
		this.helpMsg = Constants.REMOVEQUEUE_HELP;
		this.description = Constants.REMOVEQUEUE_DESC;
		this.name = Constants.REMOVEQUEUE_NAME;
		this.adminRequired = true;
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try {
			if (!args.isEmpty()) {
				for (String a : args) {
					try {
						qm.removeQueue(Integer.valueOf(a));
					} catch (NumberFormatException ex) {
						qm.removeQueue(a);
					}
				}
			}else{
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Functions.createMessage(String.format("Queue %s removed", args.get(0)), qm.getHeader(), true);
			System.out.println(successMsg);
		} catch (DoesNotExistException | BadArgumentsException ex) {
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}

	}

}
