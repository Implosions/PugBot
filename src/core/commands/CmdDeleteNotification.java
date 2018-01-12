package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
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
					qm.removeNotification(member.getUser());
				}else{
					try{
						qm.removeNotification(member.getUser(), Integer.valueOf(args[0]));
					}catch(NumberFormatException ex){
						qm.removeNotification(member.getUser(), args[0]);
					}
				}
			}else{
				throw new BadArgumentsException();
			}
			this.response = Utils.createMessage("Notification(s) removed", "", true);
			System.out.println(success());
			qm.saveToFile();
		}catch(BadArgumentsException | DoesNotExistException ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}
	
}
