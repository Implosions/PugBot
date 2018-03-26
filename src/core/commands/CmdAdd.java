package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdAdd extends Command{
	
	public CmdAdd(){
		this.helpMsg = Constants.ADD_HELP;
		this.description = Constants.ADD_DESC;
		this.name = Constants.ADD_NAME;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (args.length == 0) {
			qm.addPlayerToQueue(member.getUser());
		} else {
			for (String q : args) {
				try {
					qm.addPlayerToQueue(member.getUser(), Integer.valueOf(q));
				} catch (NumberFormatException ex) {
					qm.addPlayerToQueue(member.getUser(), q);
				}
			}
		}
		qm.updateTopic();
		if (qm.hasPlayerJustFinished(member.getUser())) {
			this.response = Utils.createMessage(String.format("%s added to queue", member.getEffectiveName()),
					String.format("Your game has just finished, you will be randomized into queue after %d seconds",
							qm.getServer().getSettings().finishTime()), true);
		} else {
			this.response = Utils.createMessage(String.format("%s added to queue", member.getEffectiveName()),
					qm.getHeader(), true);
		}
		System.out.println(success());
		return response;
	}
}
