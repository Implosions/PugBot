package core.commands;

import java.awt.Color;
import java.util.List;

import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class CmdHelp extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		String prefix = "!";
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		embedBuilder.setColor(Color.green);
		
		if (args.length == 0) {
			List<ICommand> cmdList = server.getCommandManager().getCommandList();
			
			cmdList.sort((ICommand c1, ICommand c2) -> c1.getName().compareTo(c2.getName()));
			
			String currentLetter = "";
			String cmds = "";
			boolean admin = server.isAdmin(caller);
			
			for(ICommand cmd : cmdList){
				String helpLine = String.format("%s**%s** - %s%n", prefix, cmd.getName(), cmd.getDescription());
				String letter = String.valueOf(cmd.getName().charAt(0)).toUpperCase();
				
				
				if(cmd.isAdminRequired() && !admin){
					continue;
				}
				
				if(!currentLetter.equals(letter)){
					embedBuilder.addField(new Field(currentLetter, cmds, false));
					cmds = "";
					currentLetter = letter;
				}
				
				cmds += helpLine;
			}
			
			embedBuilder.setTitle("Commands");
			caller.getUser().openPrivateChannel().complete().sendMessage(embedBuilder.build()).queue();
			
			return Utils.createMessage("`Help sent`");
			
		} else {
			String cmdName = args[0];
			
			if(!server.getCommandManager().doesCommandExist(cmdName)){
				throw new InvalidUseException("Command does not exist");
			}
			
			ICommand cmd = server.getCommandManager().getCommand(cmdName);
			
			
			String help = String.format("%s%n%n%s", cmd.getDescription(), cmd.getHelp());
			embedBuilder.addField(new Field(cmd.getName(), help, false));
			
			return new MessageBuilder().setEmbed(embedBuilder.build()).build();
		}
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
		return "Help";
	}

	@Override
	public String getDescription() {
		return "Gives information about this bot's commands and functions";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Lists all available commands\n" +
				getBaseCommand() + " <command name> - Lists information about a specific command";
	}
}
