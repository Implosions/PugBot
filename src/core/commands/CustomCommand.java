package core.commands;

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
	public Message execCommand(Member caller, String[] args) {
		return response;
	}

}
