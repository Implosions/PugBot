package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import bullybot.errors.BadArgumentsException;
import bullybot.errors.DoesNotExistException;
import net.dv8tion.jda.core.entities.Member;

public class CmdEditQueue extends Command{
	
	public CmdEditQueue(){
		this.helpMsg = Info.EDITQUEUE_HELP;
		this.successMsg = Info.EDITQUEUE_SUCCESS;
		this.description = Info.EDITQUEUE_DESC;
		this.adminRequired = true;
		this.name = "editqueue";
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			if(args.size() == 3 && Integer.valueOf(args.get(2)) != null){
				try{
					qm.editQueue(Integer.valueOf(args.get(0)), args.get(1), Integer.valueOf(args.get(2)));
				}catch(NumberFormatException ex){
					try{
						qm.editQueue(args.get(0), args.get(1), Integer.valueOf(args.get(2)));
					}catch(NumberFormatException e){
						throw new BadArgumentsException("New max player count must be a valid number");
					}
				}
			}else{
				throw new BadArgumentsException();
			}
			qm.updateTopic();
			this.response = Functions.createMessage(String.format("Queue %s edited", args.get(0)), qm.getHeader(), true);
			System.out.println(successMsg);
		}catch(BadArgumentsException | DoesNotExistException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
