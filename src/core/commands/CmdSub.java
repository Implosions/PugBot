package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdSub extends Command{
	
	public CmdSub(){
		this.helpMsg = Constants.SUB_HELP;
		this.description = Constants.SUB_DESC;
		this.name = Constants.SUB_NAME;
		this.pugChannelOnlyCommand = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		String targName, subName;
		QueueManager qm = server.getQueueManager();
		if (args.length < 3) {
			Member target = server.getMember(args[0]);
			Member substitute = null;

			if (args.length == 1) {
				substitute = member;
			} else {
				substitute = server.getMember(args[1]);
			}

			if (target != null) {
				if (substitute != null) {
					qm.sub(target.getUser(), substitute.getUser());
					targName = target.getEffectiveName();
					subName = substitute.getEffectiveName();
				} else {
					throw new BadArgumentsException("Substitute player does not exist");
				}
			} else {
				throw new BadArgumentsException("Target player does not exist");
			}
		} else {
			throw new BadArgumentsException();
		}
		qm.updateTopic();
		this.response = Utils.createMessage(String.format("%s has been subbed with %s", targName, subName), "", true);
		System.out.println(success());
		
		return response;
	}
}
