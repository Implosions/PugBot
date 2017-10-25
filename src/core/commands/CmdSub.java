package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdSub extends Command{
	
	public CmdSub(){
		this.helpMsg = Constants.SUB_HELP;
		this.description = Constants.SUB_DESC;
		this.name = Constants.SUB_NAME;
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			if(args.size() == 2){
				qm.sub(args.get(0), args.get(1));
			}else{
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Functions.createMessage(String.format("%s has been subbed with %s", args.get(0), args.get(1)), "", true);
			System.out.println(successMsg);
		}catch(DoesNotExistException | BadArgumentsException | InvalidUseException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
		
	}

}
