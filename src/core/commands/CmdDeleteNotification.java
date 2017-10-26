package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
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
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try{
			if(args.length <= 1){
				if(args.length == 0){
					qm.removeNotifications(member.getUser());
				}else{
					try{
						qm.removeNotifications(member.getUser(), Integer.valueOf(args[0]));
					}catch(NumberFormatException ex){
						qm.removeNotifications(member.getUser(), args[1]);
					}
				}
			}else{
				throw new BadArgumentsException();
			}
			this.response = Functions.createMessage("Notification(s) removed", "", true);
			System.out.println(successMsg);
			qm.saveToFile();
		}catch(BadArgumentsException | DoesNotExistException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}
	
}
