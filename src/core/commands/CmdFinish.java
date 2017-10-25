package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.exceptions.InvalidUseException;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdFinish extends Command{
	
	public CmdFinish(){
		this.helpMsg = Constants.FINISH_HELP;
		this.successMsg = Constants.FINISH_SUCCESS;
		this.description = Constants.FINISH_DESC;
		this.name = "finish";
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			qm.finish(member.getUser());
			qm.updateTopic();
			this.response = Functions.createMessage(successMsg, qm.getHeader(), true);
			System.out.println(successMsg);
		}catch(InvalidUseException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
