package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.functions.Stuff;
import bullybot.errors.DoesNotExistException;
import bullybot.errors.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;

public class CmdAdd extends Command{
	
	public CmdAdd(){
		this.helpMsg = Info.ADD_HELP;
		this.successMsg = Info.ADD_SUCCESS;
		this.description = Info.ADD_DESC;
		this.name = "add";
	}
	
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try{
			if(args.isEmpty()){
				qm.addPlayer(member.getUser());
			}else{
				for(String q : args){
					try {
						qm.addPlayer(member.getUser(), Integer.valueOf(q));
					} catch (NumberFormatException ex) {
						qm.addPlayer(member.getUser(), q);
					}
				}
			}
			qm.updateTopic();
			if(qm.hasPlayerJustFinished(member.getUser())){
				this.response = Stuff.createMessage(successMsg, "Your game has just finished, you will be randomized into queue after 60 seconds", true);
			}else{
				this.response = Stuff.createMessage(String.format("%s added to queue", member.getEffectiveName()), qm.getHeader(), true);
			}
			System.out.println(successMsg);
		}catch(DoesNotExistException | InvalidUseException ex){
			this.response = Stuff.createMessage("Error!", ex.getMessage(), false);
		}
	}
	
}
