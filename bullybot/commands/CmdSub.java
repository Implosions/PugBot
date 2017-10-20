package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import bullybot.errors.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;

public class CmdSub extends Command{
	
	public CmdSub(){
		this.helpMsg = Info.SUB_HELP;
		this.successMsg = Info.SUB_SUCCESS;
		this.description = Info.SUB_DESC;
		this.name = "sub";
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
