package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDel extends Command {

	public CmdDel() {
		this.helpMsg = Constants.DEL_HELP;
		this.description = Constants.DEL_DESC;
		this.name = Constants.DEL_NAME;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (qm.isPlayerInQueue(member.getUser()) || qm.isPlayerWaiting(member.getUser())) {
			if (args.length == 0) {
				qm.deletePlayer(member.getUser());
			} else {
				for (String q : args) {
					try {
						qm.deletePlayer(member.getUser(), Integer.valueOf(q));
					} catch (NumberFormatException ex) {
						qm.deletePlayer(member.getUser(), q);
					}
				}
			}
		} else {
			throw new InvalidUseException("You are not in any queue");
		}
		qm.updateTopic();
		this.response = Utils.createMessage(String.format("%s deleted from queue", member.getEffectiveName()),
				qm.getHeader(), true);
		System.out.println(success());
		
		return response;
	}
}
