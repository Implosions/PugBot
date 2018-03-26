package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdLoadSettings extends Command{
	
	public CmdLoadSettings(){
		this.name = Constants.LOADSETTINGS_NAME;
		this.helpMsg = Constants.LOADSETTINGS_HELP;
		this.description = Constants.LOADSETTINGS_DESC;
		this.adminRequired = true;
		this.pugCommand = false;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		server.getSettings().loadSettingsFile();
		this.response = Utils.createMessage("`Settings loaded`");
		System.out.println(success());
		
		return response;
	}

}
