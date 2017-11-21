package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdFinish extends Command{
	
	public CmdFinish(){
		this.helpMsg = Constants.FINISH_HELP;
		this.description = Constants.FINISH_DESC;
		this.name = Constants.FINISH_NAME;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try{
			qm.finish(member.getUser());
			qm.updateTopic();
			this.response = Utils.createMessage("Game finished", qm.getHeader(), true);
			System.out.println(success());
		}catch(InvalidUseException ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
