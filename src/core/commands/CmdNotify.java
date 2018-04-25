package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdNotify extends Command{
	
	public CmdNotify(){
		this.helpMsg = Constants.NOTIFY_HELP;
		this.description = Constants.NOTIFY_DESC;
		this.name = Constants.NOTIFY_NAME;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (args.length == 2) {
			Integer playerCount;
			try {
				playerCount = Integer.valueOf(args[1]);
			} catch (NumberFormatException ex) {
				throw new BadArgumentsException();
			}
			try {
				qm.addNotification(member.getUser(), Integer.valueOf(args[0]), playerCount);
			} catch (NumberFormatException ex) {
				qm.addNotification(member.getUser(), args[0], playerCount);
			}
		} else {
			throw new BadArgumentsException();
		}
		this.response = Utils.createMessage("Notification added", "", true);
		System.out.println(success());
		
		return response;
	}
}
