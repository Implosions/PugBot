package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.functions.Stuff;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import bullybot.errors.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;

public class CmdRemove extends Command {

	public CmdRemove() {
		this.helpMsg = Info.REMOVE_HELP;
		this.successMsg = Info.REMOVE_SUCCESS;
		this.description = Info.REMOVE_DESC;
		this.adminRequired = true;
		this.name = "remove";
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try {
			if (args.size() > 0) {
				String name = args.get(0);
				args.remove(0);
				
				if (args.size() == 0) {
					qm.remove(name);
				} else {
					for (String s : args) {
						try {
							qm.remove(name, Integer.valueOf(s));
						} catch (NumberFormatException ex) {
							qm.remove(name, s);
						}
					}
				}

			} else {
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Stuff.createMessage(String.format("Player removed from queue"), qm.getHeader(), true);
			System.out.println(successMsg);
		} catch (BadArgumentsException | DoesNotExistException | InvalidUseException ex) {
			this.response = Stuff.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
