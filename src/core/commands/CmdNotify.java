package core.commands;

import core.Constants;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdNotify extends Command{
	
	public CmdNotify(){
		this.helpMsg = Constants.NOTIFY_HELP;
		this.description = Constants.NOTIFY_DESC;
		this.name = Constants.NOTIFY_NAME;
	}
	
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try{
			if(args.length == 2){
				Integer playerCount;
				try{
					playerCount = Integer.valueOf(args[1]);
				}catch(NumberFormatException ex){
					throw new BadArgumentsException();
				}
				try{
					qm.addNotification(member.getUser(), Integer.valueOf(args[0]), playerCount);
				}catch(NumberFormatException ex){
					qm.addNotification(member.getUser(), args[0], playerCount);
				}
			}else{
				throw new BadArgumentsException();
			}
			this.response = Functions.createMessage("Notification added", "", true);
			System.out.println(successMsg);
			qm.saveToFile();
		}catch(BadArgumentsException | DoesNotExistException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
