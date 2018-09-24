package core;

import java.util.Arrays;
import java.util.HashSet;

import core.commands.ICommand;
import core.entities.MenuRouter;
import core.entities.Server;
import core.entities.ServerManager;
import core.util.Utils;
import core.exceptions.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

// EventHandler class

public class EventHandler extends ListenerAdapter {

	public EventHandler(JDA jda) {
		new ServerManager(jda);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Server server = ServerManager.getServer(event.getGuild().getIdLong());
		String message = event.getMessage().getContent();
		if (message.startsWith("!") && message.length() > 1 && !event.getAuthor().isBot()) {

			// Check if member is banned or the input is spam
			if (server.isBanned(event.getMember()) || server.isSpam(event.getMessage())) {
				return;
			}

			// Workaround for users with spaces in their name
			// Replaces name with user id
			if (event.getMessage().getMentionedUsers().size() > 0) {
				for (User u : event.getMessage().getMentionedUsers()) {
					message = message.replace("@" + event.getGuild().getMemberById(u.getId()).getEffectiveName(),
							u.getId());
				}
			}

			// Replaces standard emote string
			// Allows bot to use server specific emotes
			if (event.getMessage().getEmotes().size() > 0) {
				// Uses a set to remove duplicates
				HashSet<Emote> emotes = new HashSet<Emote>(event.getMessage().getEmotes());
				for (Emote emote : emotes) {
					message = message.replace(":" + emote.getName() + ":",
							String.format("<:%s:%s>", emote.getName(), emote.getId()));
				}
			}

			String[] tokens = message.substring(1).split(" ");

			// Log input
			System.out.println("Command input: " + event.getAuthor().toString() + " " + Arrays.toString(tokens));

			String cmdName = tokens[0].toLowerCase();
			String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

			processCommand(server, event.getChannel(), event.getMember(), cmdName, args);
		}
		// Updates Server.activityList
		server.updateActivityList(event.getMember());
	}
	
	private void processCommand(Server server, TextChannel channel, Member caller, String cmdName, String[] args){
		Message response = null;
		ICommand cmd = null;

		try{
			// Check if command is valid
			if(!server.getCommandManager().doesCommandExist(cmdName)){
				throw new InvalidUseException("Command does not exist.\n"
											+ "Use the **Help** command to see all available commands");
			}
			
			cmd = server.getCommandManager().getCommand(cmdName);
			
			// Check if command is in the correct channel
			if(!cmd.isGlobalCommand() && channel != server.getPugChannel()){
				return;
			}
			
			// Check if admin is required
			if (cmd.isAdminRequired() && !server.isAdmin(caller)){
				throw new InvalidUseException("Admin is required for this command");
			}
			
			// Execute command
			response = cmd.execCommand(caller, args);
			
		} catch(InvalidUseException ex) {
			response = Utils.createMessage("Error!", ex.getMessage(), false);
		} catch (BadArgumentsException ex) {
			response = Utils.createMessage("Error!",
					String.format("%s%nUsage: %s", ex.getMessage(), cmd.getHelp()), false);
		} catch (Exception ex) {
			response = Utils.createMessage("Error!", "Something went wrong", false);
			ex.printStackTrace();
		}
		
		if(response != null) {
			channel.sendMessage(response).queue();
		}
	}

	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		Member m = event.getGuild().getMember(event.getUser());
		// Passes online status if a player goes offline
		if (m.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
			ServerManager.getServer(event.getGuild().getIdLong()).playerDisconnect(m);
		}
	}

	public void onGuildJoin(GuildJoinEvent event) {
		// Adds the new server to the server list
		ServerManager.addNewServer(event.getGuild());
		System.out.println(String.format("Joined server: %s", event.getGuild().getName()));
	}

	public void onGuildLeave(GuildLeaveEvent event) {
		// Removes the server from the server list
		ServerManager.removeServer(event.getGuild().getIdLong());
		System.out.println(String.format("Removed from server: %s", event.getGuild().getName()));
	}

	public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
		// Updates activity list with the user
		ServerManager.getServer(event.getGuild().getIdLong()).updateActivityList(event.getMember());
	}

	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		// Inserts new player into database
		Database.insertPlayer(event.getUser().getIdLong(), event.getMember().getEffectiveName());
	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		if(!event.getUser().isBot()){
			MenuRouter.newReactionEvent(event.getMessageIdLong(), event.getReactionEmote().getName());
		}
	}
}
