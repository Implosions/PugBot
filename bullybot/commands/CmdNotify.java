package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import net.dv8tion.jda.core.entities.Member;

public class CmdNotify extends Command{
	
	public CmdNotify(){
		this.helpMsg = Info.NOTIFY_HELP;
		this.description = Info.NOTIFY_DESC;
		this.successMsg = Info.NOTIFY_SUCCESS;
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
