package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.functions.Stuff;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import net.dv8tion.jda.core.entities.Member;

public class CmdRemoveQueue extends Command {

	public CmdRemoveQueue() {
		this.helpMsg = Info.REMOVEQUEUE_HELP;
		this.successMsg = Info.REMOVEQUEUE_SUCCESS;
		this.description = Info.REMOVEQUEUE_DESC;
		this.adminRequired = true;
		this.name = "removequeue";
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
			this.response = Stuff.createMessage(String.format("Queue %s removed", args.get(0)), qm.getHeader(), true);
			System.out.println(successMsg);
		} catch (DoesNotExistException | BadArgumentsException ex) {
			this.response = Stuff.createMessage("Error!", ex.getMessage(), false);
		}

	}

}
