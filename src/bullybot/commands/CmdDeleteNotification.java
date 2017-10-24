package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import net.dv8tion.jda.core.entities.Member;

public class CmdDeleteNotification extends Command{
	
	public CmdDeleteNotification(){
		this.helpMsg = Info.DELETENOTIFICATION_HELP;
		this.successMsg = Info.DELETENOTIFICATION_SUCCESS;
		this.description = Info.DELETENOTIFICATION_DESC;
		this.name = "deletenotification";
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
