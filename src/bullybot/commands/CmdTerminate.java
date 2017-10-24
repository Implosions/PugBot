package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import net.dv8tion.jda.core.entities.Member;

public class CmdTerminate extends Command{
	public CmdTerminate(){
		this.name = "terminate";
		this.helpMsg = Info.TERMINATE_HELP;
		this.description = Info.TERMINATE_DESC;
		this.adminRequired = true;
	}
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		System.out.println("Terminating bot...");
		System.exit(0);
	}

}
