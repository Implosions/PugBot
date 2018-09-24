package core.commands;

import core.entities.Server;
import net.dv8tion.jda.core.entities.Message;

// Command abstract class

public abstract class Command implements ICommand {

	protected Server server;
	protected Message response;
	
	public Command(Server server){
		this.server = server;
	}
	
	protected String getBaseCommand(){
		return "!" + getName();
	}
}
