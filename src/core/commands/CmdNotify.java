package core.commands;

import core.Constants;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
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
			Queue queue;
			try {
				playerCount = Integer.valueOf(args[1]);
				if(playerCount < 1){
					throw new InvalidUseException("Player count must be greater than zero");
				}
			} catch (NumberFormatException ex) {
				throw new InvalidUseException("Player count must be a valid integer");
			}
			
			try {
				queue = qm.getQueue(Integer.valueOf(args[0]));
			} catch (NumberFormatException ex) {
				queue = qm.getQueue(args[0]);
			}
			
			if(queue != null){
				if(playerCount < queue.getMaxPlayers()){
					queue.addNotification(member.getUser(), playerCount);
					this.response = Utils.createMessage(String.format(
							"Notification added to queue '%s' at %d players", queue.getName(), playerCount), "", true);
				}else{
					throw new InvalidUseException("Player count must be below the max amount of players");
				}
				
			}else{
				throw new InvalidUseException("Queue does not exist");
			}
			
		} else {
			throw new BadArgumentsException();
		}
		System.out.println(success());
		
		return response;
	}
}
