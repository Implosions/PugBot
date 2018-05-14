package core.commands;

import core.Constants;
import core.entities.Game;
import core.entities.Server;
import core.entities.Game.GameStatus;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class CmdSubCaptain extends Command{

	public CmdSubCaptain(){
		this.name = Constants.SUBCAPTAIN_NAME;
		this.description = Constants.SUBCAPTAIN_DESC;
		this.helpMsg = Constants.SUBCAPTAIN_HELP;
		this.pugChannelOnlyCommand = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if (args.length == 1) {
			QueueManager qm = server.getQueueManager();
			Member targetMember = server.getMember(args[0]);

			if (targetMember != null) {
				User target = targetMember.getUser();
				User sub = member.getUser();
				
				if(qm.isPlayerIngame(sub)){
					if(qm.isPlayerIngame(target)){
						Game g = qm.getPlayersGame(sub);
						if(g.getStatus() == GameStatus.PICKING){
							if (g.getCaptains()[0] == target || g.getCaptains()[1] == target) {
								if(g.getCaptains()[0] != sub && g.getCaptains()[1] != sub){
									g.subCaptain(sub, target);
									this.response = Utils.createMessage(String.format("`%s has replaced %s as captain`",
													member.getEffectiveName(), targetMember.getEffectiveName()));
								}else{
									throw new InvalidUseException("You are already a captain");
								}
								
							} else {
								throw new InvalidUseException(targetMember.getEffectiveName() + " is not a captain in your game");
							}
							
						}else{
							throw new InvalidUseException("Picking has finished");
						}
						
					}else{
						throw new InvalidUseException(targetMember.getEffectiveName() + " is not in-game");
					}
					
				}else{
					throw new InvalidUseException("You are not in-game");
				}
				
			} else {
				throw new InvalidUseException(args[0] + " does not exist");
			}
			
		} else {
			throw new BadArgumentsException();
		}
		System.out.println(success());
		
		return response;
	}
}
