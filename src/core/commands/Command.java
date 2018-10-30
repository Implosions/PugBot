package core.commands;

import core.entities.Server;

// Command abstract class

public abstract class Command implements ICommand {

	protected Server server;
	
	public Command(Server server){
		this.server = server;
	}
	
	protected String getBaseCommand(){
		return "!" + getName();
	}
}
