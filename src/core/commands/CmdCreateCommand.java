package core.commands;

import java.util.Arrays;

import core.Constants;
import core.Database;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdCreateCommand extends Command{

	public CmdCreateCommand(){
		this.name = Constants.CREATECOMMAND_NAME;
		this.helpMsg = Constants.CREATECOMMAND_HELP;
		this.description = Constants.CREATECOMMAND_DESC;
		this.adminRequired = true;
	}
	
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length >= 2){
			String name = args[0].toLowerCase();
			if(!server.cmds.validateCommand(name)){
				String response = joinArgs(Arrays.copyOfRange(args, 1, args.length));
			
				server.cmds.addCommand(new CustomCommand(name, response));
				Database.insertServerCustomCommand(Long.valueOf(server.getid()), name, response);
			}else{
				throw new InvalidUseException(String.format("Command '%s' already exists", name));
			}
			
		}else{
			throw new BadArgumentsException();
		}
		
		System.out.println(success());
		return Utils.createMessage(String.format("`Command '%s' created`", args[0].toLowerCase()));
	}

	private String joinArgs(String[] args){
		String message = "";
		for(String arg : args){
			message += arg + " ";
		}
		
		return message;
	}
}
