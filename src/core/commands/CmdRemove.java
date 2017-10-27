package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdRemove extends Command {

	public CmdRemove() {
		this.helpMsg = Constants.REMOVE_HELP;
		this.description = Constants.REMOVE_DESC;
		this.name = Constants.REMOVE_NAME;
		this.adminRequired = true;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try {
			if (args.length > 0) {
				String name = args[0];
				
				if (args.length == 1) {
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
			this.response = Functions.createMessage(String.format("%s removed from queue", args[0]), qm.getHeader(), true);
			System.out.println(successMsg);
		} catch (BadArgumentsException | DoesNotExistException | InvalidUseException ex) {
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
