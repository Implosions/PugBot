package core;

import java.util.Arrays;
import java.util.regex.Pattern;

import core.commands.Command;
import core.entities.Server;
import core.entities.ServerManager;
import core.util.Utils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

// EventHandler class
// TODO: move admin list to Server

public class EventHandler extends ListenerAdapter {

	public EventHandler(JDA jda) {
		new ServerManager(jda.getGuilds());
		Utils.loadAdminList();
	}
	
	
	// Executes commands based on input from discord server
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Server server = ServerManager.getServer(event.getGuild().getId());
		String message = event.getMessage().getContent();
		if (message.startsWith("!") && message.length() > 1 && !event.getAuthor().isBot()) {
			MessageChannel channel = event.getChannel();
			
			// Command spam check
			// Checks last 5 messages if the same input was submitted in the past 3 seconds
			try{
				for(Message m : channel.getHistory().retrievePast(5).complete()){
					if(!m.getId().equals(event.getMessageId()) 
							&& m.getAuthor().equals(event.getAuthor()) 
							&& m.getContent().equals(event.getMessage().getContent())
							&& m.getCreationTime().isAfter(event.getMessage().getCreationTime().minusSeconds(3))){
						return;
					}
				}
			}catch(PermissionException ex){
				ex.printStackTrace();
			}
			// Workaround for users with spaces in their name
			// Replaces name with user id
			if (event.getMessage().getMentionedUsers().size() > 0) {
				for (User u : event.getMessage().getMentionedUsers()) {
					System.out.println(event.getGuild().getMemberById(u.getId()).getEffectiveName());
					message = message.replace("@" + event.getGuild().getMemberById(u.getId()).getEffectiveName(), u.getId());
				}
			}
			
			String[] tokens = message.substring(1).split(" ");
			// Replaces user id's with names after being tokenized
			try {
				for(String s : tokens){
					if (Pattern.matches("\\d{15,}", s)) {
						s.replace(s, event.getGuild().getMemberById(s).getUser().getName());
					}
				}
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
			// Log input
			System.out.println("Command input: " + event.getAuthor().toString() + " " + tokens.toString());
			
			String cmd = tokens[0].toLowerCase();
			String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

			if (server.cmds.validateCommand(cmd)) {
				Command cmdObj = server.cmds.getCommandObj(cmd);
				// Determine which channel to send response
				if (cmdObj.getDM()) {
					channel = event.getAuthor().openPrivateChannel().complete();
				}else if(cmdObj.getPugCommand()){
					channel = server.getPugChannel();
				}
				if (cmdObj.getAdminRequired() && !Utils.isAdmin(event.getMember())) {
					channel.sendMessage(Utils.createMessage("Error!", "Admin required", false)).queue();
				} else {
					// Executes command and sends response to proper channel
					cmdObj.execCommand(server, event.getMember(), args);
					channel.sendMessage(cmdObj.getResponse()).queue();
					// Gets message id of response after it is sent
					try{
						cmdObj.setLastResponseId(channel.getHistory().retrievePast(1).complete().get(0).getId());
					}catch(PermissionException ex){
						ex.printStackTrace();
					}
				}
			} else {
				// Will only respond to invalid commands in the pug channel
				if(channel.equals(server.getPugChannel())){
					channel.sendMessage(Utils.createMessage("Error!", "Invalid command", false)).queue();
				}
			}
		}
		server.updateActivityList(event.getAuthor());
	}
	
	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		Member m = event.getGuild().getMember(event.getUser());
		if(m.getOnlineStatus().equals(OnlineStatus.OFFLINE)){
			ServerManager.getServer(event.getGuild().getId()).playerDisconnect(m);
		}
	}

	public void onGuildJoin(GuildJoinEvent event) {
		ServerManager.addNewServer(event.getGuild());
		System.out.println(String.format("Joined server: %s", event.getGuild().getName()));
	}

	public void onGuildLeave(GuildLeaveEvent event) {
		ServerManager.removeServer(event.getGuild());
		System.out.println(String.format("Removed from server: %s", event.getGuild().getName()));
	}

	public void onGenericMessageReaction(GenericMessageReactionEvent event) {
		ServerManager.getServer(event.getGuild().getId()).updateActivityList(event.getUser());
	}
	
	public void onGenericGuild(GenericGuildEvent event){
		ServerManager.getServer(event.getGuild().getId()).setGuild(event.getGuild());
	}
}
