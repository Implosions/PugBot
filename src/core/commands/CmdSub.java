package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
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
					if(!qm.isPlayerIngame(substitute.getUser())){
						if(qm.isPlayerIngame(target.getUser())){
							qm.getPlayersGame(target.getUser()).sub(target.getUser(), substitute.getUser());
							qm.purgeQueue(substitute.getUser());
							
							this.response = Utils.createMessage(
							String.format("%s has been substituted with %s",
									target.getEffectiveName(), substitute.getEffectiveName()), "", true);
							qm.updateTopic();
						}else{
							throw new InvalidUseException(target.getEffectiveName() + " is not in-game");
						}
						
					}else{
						throw new InvalidUseException(substitute.getEffectiveName() + " is already in-game");
					}
					
				} else {
					throw new InvalidUseException("Substitute player does not exist");
				}
				
			} else {
				throw new InvalidUseException("Target player does not exist");
			}
			
		} else {
			throw new BadArgumentsException();
		}
		System.out.println(success());
		
		return response;
	}
}
