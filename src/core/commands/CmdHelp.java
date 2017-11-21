package core.commands;

import core.Constants;
import core.entities.Commands;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;

public class CmdHelp extends Command{
	
	private Commands cmds;
	
	public CmdHelp(){
		this.helpMsg = Constants.HELP_HELP;
		this.description = Constants.HELP_DESC;
		this.name = Constants.HELP_NAME;
		this.dm = true;
	}
	@Override
	public void execCommand(Server server, Member member, String[] args) {
		try{
			cmds = server.cmds;
			if(args.length == 0){
				this.response = Utils.createMessage(helpBuilder(member));
			}else{
				if(cmds.validateCommand(args[0])){
					this.response = Utils.createMessage(cmds.getCommandObj(args[0]).help());
				}
			}
			System.out.println(success());
		}catch(Exception ex){
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		}
	}
	
	private String helpBuilder(Member member){
		String s = "Commands:\n\n";
		Command cmdObj;
		
		// List commands
		for(String cmd : cmds.getCmds()){
			cmdObj = cmds.getCommandObj(cmd);
			s += String.format("!%s - %s. Usage: %s%n", cmdObj.getName(), cmdObj.getDescription(), cmdObj.help()); 
		}
		
		// List admin commands
		if(Utils.isAdmin(member)){
			s += "\nAdmin commands:\n\n";
			for(String cmd : cmds.getAdminCmds()){
				cmdObj = cmds.getCommandObj(cmd);
				s += String.format("!%s - %s. Usage: %s%n", cmdObj.getName(), cmdObj.getDescription(), cmdObj.help()); 
			}
		}
		
		s = String.format("```%s```", s);
		return s;
	}
}
