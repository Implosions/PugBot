package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import net.dv8tion.jda.core.entities.Member;

public class CmdTerminate extends Command{
	public CmdTerminate(){
		this.name = "terminate";
		this.helpMsg = Constants.TERMINATE_HELP;
		this.description = Constants.TERMINATE_DESC;
		this.adminRequired = true;
	}
	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		System.out.println("Terminating bot...");
		System.exit(0);
	}

}
