package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DuplicateEntryException;
import net.dv8tion.jda.core.entities.Member;

public class CmdCreateQueue extends Command {

	public CmdCreateQueue() {
		this.helpMsg = Info.CREATEQUEUE_HELP;
		this.successMsg = Info.CREATEQUEUE_SUCCESS;
		this.description = Info.CREATEQUEUE_DESC;
		this.name = "createqueue";
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
