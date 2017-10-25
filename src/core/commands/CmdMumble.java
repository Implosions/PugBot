package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdMumble extends Command{
	
	public CmdMumble(){
		this.helpMsg = Constants.MUMBLE_HELP;
		this.description = Constants.MUMBLE_DESC;
		this.name = "mumble";
		this.successMsg = Constants.MUMBLE_SUCCESS;
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		this.response = Functions.createMessage("", qm.getServer().getSettings().mumble(), true);
	}

}
