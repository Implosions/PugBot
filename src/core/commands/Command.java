package core.commands;

import core.entities.Server;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

// Command abstract class

public abstract class Command {
	protected String name;
	protected String helpMsg;
	protected String successMsg = "Command " + name + " completed.";
	protected String description;
	protected Message response;
	protected String lastResponseId = null;
	protected boolean dm = false;
	protected boolean adminRequired = false;
	protected boolean pugCommand = true;
	
	public abstract void execCommand(Server server, Member member, String[] args);
	
	public String help(){
		return this.helpMsg;
	}
	
	public Message getResponse(){
		return this.response;
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
	
	public boolean getPugCommand(){
		return pugCommand;
	}
	
	public void setLastResponseId(String id){
		lastResponseId = id;
	}
}
