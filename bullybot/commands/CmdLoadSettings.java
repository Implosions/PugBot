package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import net.dv8tion.jda.core.entities.Member;

public class CmdLoadSettings extends Command{
	
	public CmdLoadSettings(){
		this.name = "loadsettings";
		this.helpMsg = Info.LOADSETTINGS_HELP;
		this.description = Info.LOADSETTINGS_DESC;
		this.adminRequired = true;
		this.pugCommand = false;
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		qm.getServer().getSettings().loadSettingsFile();
		this.response = Functions.createMessage("`Settings loaded`");
	}

}
