package core.commands;

import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CustomCommand extends Command {

	private String name;
	private Message output;
	
	
	public CustomCommand(Server server, String name, String output){
		super(server);
		this.name = name;		
		this.output = Utils.createMessage(output);
	}
	
	@Override
	public Message execCommand(Member caller, String[] args) {
		return output;
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getHelp() {
		return getBaseCommand();
	}

}
