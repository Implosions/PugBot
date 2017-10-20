package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdMumble extends Command{
	
	public CmdMumble(){
		this.helpMsg = Info.MUMBLE_HELP;
		this.description = Info.MUMBLE_DESC;
		this.name = "mumble";
		this.successMsg = Info.MUMBLE_SUCCESS;
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		this.response = Functions.createMessage("", successMsg, true);
	}

}
