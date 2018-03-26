package core;

import java.util.Arrays;

import core.commands.Command;
import core.entities.Server;
import core.entities.ServerManager;
import core.util.Utils;
import core.exceptions.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

// EventHandler class

public class EventHandler extends ListenerAdapter {

	public EventHandler(JDA jda) {
		new ServerManager(jda.getGuilds());
	}
	
	
	/**
	 * Gets message sent from guild, checks if the message is a command, tokenizes the arguments, and executes the command.
	 */
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Server server = ServerManager.getServer(event.getGuild().getId());
		String message = event.getMessage().getContent();
		if (server != null && message.startsWith("!") && message.length() > 1 && !event.getAuthor().isBot()) {
			
			// Check if member is banned, return if true
			if(server.isBanned(event.getMember())){
				return;
			}
			
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
					message = message.replace("@" + event.getGuild().getMemberById(u.getId()).getEffectiveName(), u.getId());
				}
			}
			
			String[] tokens = message.substring(1).split(" ");
			
			// Log input
			System.out.println("Command input: " + event.getAuthor().toString() + " " + Arrays.toString(tokens));
			
			String cmd = tokens[0].toLowerCase();
			String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

			if (server.cmds.validateCommand(cmd)) {
				Command cmdObj = server.cmds.getCommandObj(cmd);
				// Determine which channel to send response
				if (cmdObj.getDM()){
					channel = event.getAuthor().openPrivateChannel().complete();
				}else if(cmdObj.getPugCommand()){
					channel = server.getPugChannel();
				}
				if (cmdObj.getAdminRequired() && !server.isAdmin(event.getMember())) {
					channel.sendMessage(Utils.createMessage("Error!", "Admin required", false)).queue();
				} else {
					// Executes command and sends response to proper channel
					Message response;
					try{
						response = cmdObj.execCommand(server, event.getMember(), args);
					}catch(BadArgumentsException | DoesNotExistException | DuplicateEntryException | InvalidUseException ex){
						response = Utils.createMessage("Error!", ex.getMessage(), false);
					}catch(Exception ex){
						response = Utils.createMessage("Error!", "Something went wrong", false);
						System.out.println(ex.getMessage());
					}
					
					try{
						Message sentMsg = channel.sendMessage(response).complete();
						
						cmdObj.setLastResponseId(sentMsg.getId());
					}catch(Exception ex){
						System.out.println("Error sending message.\n" + ex.getMessage());
					}
				}
			} else {
				// Will only respond to invalid commands in the pug channel
				if(channel.equals(server.getPugChannel())){
					channel.sendMessage(Utils.createMessage("Error!", "Invalid command", false)).queue();
				}
			}
		}
		// Updates Server.activityList
		server.updateActivityList(event.getAuthor());
	}
	
	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		Member m = event.getGuild().getMember(event.getUser());
		// Passes online status if a player goes offline
		if(m.getOnlineStatus().equals(OnlineStatus.OFFLINE)){
			ServerManager.getServer(event.getGuild().getId()).playerDisconnect(m);
		}
	}
	
	public void onGuildJoin(GuildJoinEvent event) {
		// Adds the new server to the server list
		ServerManager.addNewServer(event.getGuild());
		System.out.println(String.format("Joined server: %s", event.getGuild().getName()));
	}
	
	public void onGuildLeave(GuildLeaveEvent event) {
		// Removes the server from the server list
		ServerManager.removeServer(event.getGuild());
		System.out.println(String.format("Removed from server: %s", event.getGuild().getName()));
	}

	public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
		// Updates activity list with the user
		ServerManager.getServer(event.getGuild().getId()).updateActivityList(event.getUser());
	}
	
	public void onGenericGuild(GenericGuildEvent event){
		// Updates Server's Guild object with any changes
		ServerManager.getServer(event.getGuild().getId()).setGuild(event.getGuild());
	}
	
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		// Inserts new player into database
		Database.insertPlayer(event.getUser().getIdLong(), event.getMember().getEffectiveName());
	}
}
