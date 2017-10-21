package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
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
				this.response = Functions.createMessage(String.format("%s added to queue", member.getEffectiveName()), String.format("Your game has just finished, you will be randomized into queue after %d seconds", qm.getServer().getSettings().finishTime()), true);
			}else{
				this.response = Functions.createMessage(String.format("%s added to queue", member.getEffectiveName()), qm.getHeader(), true);
			}
			System.out.println(successMsg);
		}catch(DoesNotExistException | InvalidUseException ex){
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}
	
}
