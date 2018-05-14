package core.commands;

import core.Constants;
import core.entities.Game;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdFinish extends Command{
	
	public CmdFinish(){
		this.helpMsg = Constants.FINISH_HELP;
		this.description = Constants.FINISH_DESC;
		this.name = Constants.FINISH_NAME;
		this.pugChannelOnlyCommand = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		if(qm.isPlayerIngame(member.getUser())){
			for(Queue queue : qm.getQueueList()){
				for(Game game : queue.getGames()){
					if(game.getPlayers().contains(member.getUser())){
						queue.finish(game);
						this.response = Utils.createMessage(String.format("`Game '%s' finished`", game.getName()));
						break;
					}
				}
			}
		}else{
			throw new InvalidUseException("You are not in-game");
		}
		System.out.println(success());
		
		return response;
	}
}
