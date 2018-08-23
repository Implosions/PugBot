package core.commands;

import core.entities.Server;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

// Command abstract class

public abstract class Command {
	protected String name;
	protected String helpMsg;
	protected String description;
	protected Message response;
	protected String lastResponseId = null;
	protected boolean dm = false;
	protected boolean adminRequired = false;
	protected boolean pugChannelOnlyCommand = false;
	protected Server server;
	
	public abstract Message execCommand(Member caller, String[] args);
	
	public String help(){
		return this.helpMsg;
	}
	
	public boolean getDM(){
		return dm;
	}
	
	public boolean getAdminRequired(){
		return adminRequired;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public boolean isPugChannelOnlyCommand(){
		return pugChannelOnlyCommand;
	}
	
	public void setLastResponseId(String id){
		lastResponseId = id;
	}
}
