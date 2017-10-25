package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.DoesNotExistException;
import core.exceptions.InvalidUseException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdDel extends Command{
	
	public CmdDel(){
		this.helpMsg = Constants.DEL_HELP;
		this.successMsg = Constants.DEL_SUCCESS;
		this.description = Constants.DEL_DESC;
		this.name = "del";
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			if(args.isEmpty()){
				qm.deletePlayer(member.getUser());
			}else{
				for(String q : args){
					try {
						qm.deletePlayer(member.getUser(), Integer.valueOf(q));
					} catch (NumberFormatException ex) {
						qm.deletePlayer(member.getUser(), q);
					}
				}
			}
			qm.updateTopic();
			this.response = Functions.createMessage(String.format("%s deleted from queue", member.getEffectiveName()), qm.getHeader(), true);
			System.out.println(successMsg);
		}catch(DoesNotExistException | InvalidUseException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
