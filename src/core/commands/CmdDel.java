package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdDel extends Command{
	
	public CmdDel(){
		this.helpMsg = Constants.DEL_HELP;
		this.description = Constants.DEL_DESC;
		this.name = Constants.DEL_NAME;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try{
			if(args.length == 0){
				qm.deletePlayer(member.getUser());
			}else{
				for(String q : args){
					try {
						qm.deletePlayer(member.getUser(), Integer.valueOf(q));
					} catch (NumberFormatException ex) {
						qm.deletePlayer(member.getUser(), q);
					}
				}
			}
			qm.updateTopic();
			this.response = Utils.createMessage(String.format("%s deleted from queue", member.getEffectiveName()), qm.getHeader(), true);
			System.out.println(successMsg);
		}catch(DoesNotExistException | InvalidUseException ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
