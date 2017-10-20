package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.functions.Stuff;
import bullybot.errors.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;

public class CmdFinish extends Command{
	
	public CmdFinish(){
		this.helpMsg = Info.FINISH_HELP;
		this.successMsg = Info.FINISH_SUCCESS;
		this.description = Info.FINISH_DESC;
		this.name = "finish";
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			qm.finish(member.getUser());
			qm.updateTopic();
			this.response = Stuff.createMessage(successMsg, qm.getHeader(), true);
			System.out.println(successMsg);
		}catch(InvalidUseException ex){
			this.response = Stuff.createMessage("Error!", ex.getMessage(), false);
		}
	}

}
