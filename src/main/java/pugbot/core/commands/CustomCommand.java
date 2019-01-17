package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.Server;

public class CustomCommand extends Command {

	private String name;
	private Message output;
	
	
	public CustomCommand(Server server, String name, String output){
		setServer(server);
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
