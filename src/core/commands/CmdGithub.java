package core.commands;

import core.Constants;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdGithub extends Command{
	
	public CmdGithub(){
		this.name = Constants.GITHUB_NAME;
		this.description = Constants.GITHUB_DESC;
		this.helpMsg = Constants.GITHUB_HELP;
		this.pugCommand = false;
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		this.response = Utils.createMessage("https://github.com/Implosions/BullyBot");
		System.out.println(success());
		
		return response;
	}
}
