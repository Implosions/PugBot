package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdNotify extends Command{
	
	public CmdNotify(){
		this.helpMsg = Constants.NOTIFY_HELP;
		this.description = Constants.NOTIFY_DESC;
		this.successMsg = Constants.NOTIFY_SUCCESS;
		this.name = "notify";
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			if(args.size() == 2){
				Integer playerCount;
				try{
					playerCount = Integer.valueOf(args.get(1));
				}catch(NumberFormatException ex){
					throw new BadArgumentsException();
				}
				try{
					qm.addNotification(member.getUser(), Integer.valueOf(args.get(0)), playerCount);
				}catch(NumberFormatException ex){
					qm.addNotification(member.getUser(), args.get(0), playerCount);
				}
			}else{
				throw new BadArgumentsException();
			}
			this.response = Functions.createMessage(successMsg, "", true);
			System.out.println(successMsg);
			qm.saveToFile();
		}catch(BadArgumentsException | DoesNotExistException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
