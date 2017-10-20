package bullybot.classfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import bullybot.classfiles.functions.Stuff;
import bullybot.commands.Command;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {

	@SuppressWarnings("unused")
	private static Commands cmds;
	private static PugManager pm;

	public EventHandler(JDA jda) {
		cmds = new Commands();
		pm = new PugManager(jda);
		Stuff.loadAdminList();
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String message = event.getMessage().getContent();
		if (message.startsWith("!") && message.length() > 1 && !event.getAuthor().isBot() && !event.getAuthor().getId().equals("213935477999403008")) {
			MessageChannel channel = event.getChannel();
			
			for(Message m : channel.getHistory().retrievePast(3).complete()){
				if(!m.getId().equals(event.getMessageId()) && m.getAuthor().equals(event.getAuthor()) && m.getContent().equals(event.getMessage().getContent())){
					return;
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

			if (Commands.validateCommand(cmd)) {
				Command cmdObj = Commands.getCommandObj(cmd);
				if (cmdObj.getDM()) {
					channel = event.getAuthor().openPrivateChannel().complete();
				}else if(cmdObj.getPugCommand()){
					channel = PugManager.getPugChannel(event.getGuild().getId());
				}
				if (cmdObj.getAdminRequired() && !Stuff.isAdmin(event.getMember())) {
					channel.sendMessage(Stuff.createMessage("Error!", "Admin required", false)).queue();
				} else {
					cmdObj.execCommand(PugManager.getQueueManager(event.getGuild().getId()), event.getMember(), args);
					channel.sendMessage(cmdObj.getResponse()).queue();
				}
			} else {
				if(channel.equals(PugManager.getPugChannel(event.getGuild().getId()))){
					channel.sendMessage(Stuff.createMessage("Error!", "Invalid command", false)).queue();
				}
			}
		}
		PugManager.updateActivityList(event.getAuthor());
	}

	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		if (event.getGuild().getMember(event.getUser()).getOnlineStatus().equals(OnlineStatus.ONLINE)
				|| event.getGuild().getMember(event.getUser()).getOnlineStatus().equals(OnlineStatus.IDLE)) {
			if (pm.getDcList().contains(event.getUser())) {
				pm.getDcList().remove(event.getUser());
				System.out.println(String.format("User %s has returned", event.getUser().getName()));
			}
		} else {
			if (PugManager.getQueueManager(event.getGuild().getId()).isPlayerInQueue(event.getUser())) {
				pm.startDcTimer(event.getUser());
			}
		}
	}

	public void onGuildJoin(GuildJoinEvent event) {
		PugManager.addNewServer(event.getGuild().getId());
		System.out.println(String.format("Joined server: %s", event.getGuild().getName()));
	}

	public void onGuildLeave(GuildLeaveEvent event) {
		PugManager.removeServer(event.getGuild().getId());
		System.out.println(String.format("Removed from server: %s", event.getGuild().getName()));
	}

	public void onGenericMessageReaction(GenericMessageReactionEvent event) {
		try {
			PugManager.updateActivityList(event.getUser());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
