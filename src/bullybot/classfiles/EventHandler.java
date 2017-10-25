package bullybot.classfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import bullybot.classfiles.util.Functions;
import bullybot.commands.Command;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
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
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {

	public EventHandler(JDA jda) {
		new ServerManager(jda.getGuilds());
		Functions.loadAdminList();
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String message = event.getMessage().getContent();
		if (message.startsWith("!") && message.length() > 1 && !event.getAuthor().isBot()) {
			MessageChannel channel = event.getChannel();
			
			if(event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_HISTORY)){
				for(Message m : channel.getHistory().retrievePast(3).complete()){
					if(!m.getId().equals(event.getMessageId()) 
							&& m.getAuthor().equals(event.getAuthor()) 
							&& m.getContent().equals(event.getMessage().getContent())
							&& m.getCreationTime().isAfter(event.getMessage().getCreationTime().minusSeconds(3))){
						return;
					}
				}
			}
			
			ArrayList<String> args;

			if (event.getMessage().getMentionedUsers().size() > 0) {
				for (User u : event.getMessage().getMentionedUsers()) {
					System.out.println(event.getGuild().getMemberById(u.getId()).getEffectiveName());
					message = message.replace("@" + event.getGuild().getMemberById(u.getId()).getEffectiveName(), u.getId());
				}
			}
			args = new ArrayList<String>(Arrays.asList(message.substring(1).split(" ")));
			try {
				args.forEach((s) -> {
					if (Pattern.matches("\\d{15,}", s)) {
						Collections.replaceAll(args, s, event.getGuild().getMemberById(s).getUser().getName());
					}
				});
			} catch (NumberFormatException ex) {
				System.out.println(ex.getMessage());
			}

			System.out.println("Command input: " + event.getAuthor().toString() + " " + args.toString());
			String cmd = args.get(0).toLowerCase();
			args.remove(0);

			if (ServerManager.getServer(event.getGuild().getId()).cmds.validateCommand(cmd)) {
				Command cmdObj = ServerManager.getServer(event.getGuild().getId()).cmds.getCommandObj(cmd);
				if (cmdObj.getDM()) {
					channel = event.getAuthor().openPrivateChannel().complete();
				}else if(cmdObj.getPugCommand()){
					channel = ServerManager.getServer(event.getGuild().getId()).getPugChannel();
				}
				if (cmdObj.getAdminRequired() && !Functions.isAdmin(event.getMember())) {
					channel.sendMessage(Functions.createMessage("Error!", "Admin required", false)).queue();
				} else {
					cmdObj.execCommand(ServerManager.getServer(event.getGuild().getId()).getQueueManager(), event.getMember(), args);
					channel.sendMessage(cmdObj.getResponse()).queue();
					if(event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_HISTORY)){
						cmdObj.setLastResponseId(channel.getHistory().retrievePast(1).complete().get(0).getId());
					}
				}
			} else {
				if(channel.equals(ServerManager.getServer(event.getGuild().getId()).getPugChannel())){
					channel.sendMessage(Functions.createMessage("Error!", "Invalid command", false)).queue();
				}
			}
		}
		ServerManager.getServer(event.getGuild().getId()).updateActivityList(event.getAuthor());
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
