package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdDeleteNotification extends Command{
	
	public CmdDeleteNotification(){
		this.helpMsg = Constants.DELETENOTIFICATION_HELP;
		this.description = Constants.DELETENOTIFICATION_DESC;
		this.name = Constants.DELETENOTIFICATION_NAME;
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			if(args.size() <= 1){
				if(args.size() == 0){
					qm.removeNotifications(member.getUser());
				}else{
					try{
						qm.removeNotifications(member.getUser(), Integer.valueOf(args.get(1)));
					}catch(NumberFormatException ex){
						qm.removeNotifications(member.getUser(), args.get(1));
					}
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
