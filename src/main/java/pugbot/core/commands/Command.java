package pugbot.core.commands;

import pugbot.core.entities.Server;

// Command abstract class

public abstract class Command implements ICommand {

	protected Server server;
	
	protected String getBaseCommand(){
		String prefix = server.getSettingsManager().getCommandPrefix();
		
		return prefix + getName();
	}
	
	public void setServer(Server server) {
		this.server = server;
	}
}
