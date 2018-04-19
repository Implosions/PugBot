package core.commands;

import core.Constants;
import core.Database;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdDeleteCommand extends Command{
	
	public CmdDeleteCommand(){
		this.name = Constants.DELETECOMMAND_NAME;
		this.helpMsg = Constants.DELETECOMMAND_HELP;
		this.description = Constants.DELETECOMMAND_DESC;
		this.adminRequired = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length == 1){
			String name = args[0].toLowerCase();
			
			if(server.cmds.getCustomCmds().contains(name)){
				server.cmds.removeCommand(name);
				Database.deleteCustomCommand(Long.valueOf(server.getid()), name);
			}else{
				throw new InvalidUseException(String.format("'%s' is not a custom command", name));
			}
		}else{
			throw new BadArgumentsException();
		}
		
		System.out.println(success());
		return Utils.createMessage(String.format("Command '%s' deleted", args[0].toLowerCase()));
	}

}
