package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdSub extends Command{
	
	public CmdSub(){
		this.helpMsg = Constants.SUB_HELP;
		this.description = Constants.SUB_DESC;
		this.name = Constants.SUB_NAME;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try{
			if(args.length == 2){
				qm.sub(args[0], args[1]);
			}else{
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Utils.createMessage(String.format("%s has been subbed with %s", args[0], args[1]), "", true);
			System.out.println(successMsg);
		}catch(DoesNotExistException | BadArgumentsException | InvalidUseException ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
		
	}

}
