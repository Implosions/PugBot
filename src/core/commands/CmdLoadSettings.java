package core.commands;

import java.util.ArrayList;

import core.Constants;
import core.entities.QueueManager;
import core.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdLoadSettings extends Command{
	
	public CmdLoadSettings(){
		this.name = Constants.LOADSETTINGS_NAME;
		this.helpMsg = Constants.LOADSETTINGS_HELP;
		this.description = Constants.LOADSETTINGS_DESC;
		this.adminRequired = true;
		this.pugCommand = false;
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		qm.getServer().getSettings().loadSettingsFile();
		this.response = Functions.createMessage("`Settings loaded`");
	}

}
