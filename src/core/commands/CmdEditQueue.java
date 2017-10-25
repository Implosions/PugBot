package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.DoesNotExistException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdEditQueue extends Command{
	
	public CmdEditQueue(){
		this.helpMsg = Constants.EDITQUEUE_HELP;
		this.successMsg = Constants.EDITQUEUE_SUCCESS;
		this.description = Constants.EDITQUEUE_DESC;
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
