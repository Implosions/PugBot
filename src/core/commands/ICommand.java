package core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public interface ICommand {
	
	public Message execCommand(Member caller, String[] args);
	
	public boolean isAdminRequired();
	
	public boolean isGlobalCommand();
	
	public String getName();
	
	public String getDescription();
	
	public String getHelp();
}
