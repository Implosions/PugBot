package core.commands;

import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CustomCommand extends Command{

	public CustomCommand(String name, String response){
		this.name = name;
		this.description = "This is a custom command";
		this.helpMsg = "!" + name;
		this.response = Utils.createMessage(response);
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		return response;
	}

}
