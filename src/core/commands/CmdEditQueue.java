package core.commands;

import core.Constants;
import core.Database;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdEditQueue extends Command{
	
	public CmdEditQueue(){
		this.helpMsg = Constants.EDITQUEUE_HELP;
		this.description = Constants.EDITQUEUE_DESC;
		this.name = Constants.EDITQUEUE_NAME;
		this.adminRequired = true;
		this.pugChannelOnlyCommand = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if (args.length == 3) {
			int playerCount;
			String newName = args[1];
			Queue queue;
			
			try {
				playerCount = Integer.valueOf(args[2]);
			} catch (NumberFormatException ex) {
				throw new InvalidUseException("Player count must be a valid number");
			}
			
			try {
				queue = qm.getQueue(Integer.valueOf(args[0]));
			} catch (NumberFormatException ex) {
				queue = qm.getQueue(args[0]);
			}
			
			if(queue != null){
				if(queue.getCurrentPlayersCount() < playerCount){
					String oldName = queue.getName();
					
					queue.setName(newName);
					queue.setMaxPlayers(playerCount);
					
					Database.updateQueue(server.getid(), queue.getId(), queue.getName(), queue.getMaxPlayers());
					qm.updateTopic();
					
					this.response = Utils.createMessage(String.format("`Queue %s edited`", oldName));
				}else{
					throw new InvalidUseException("New player count must be greater than the amount of current players");
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
