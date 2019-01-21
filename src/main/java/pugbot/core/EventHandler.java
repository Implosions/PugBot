package pugbot.core;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import pugbot.core.entities.MenuRouter;
import pugbot.core.entities.Server;
import pugbot.core.entities.ServerManager;

// EventHandler class

public class EventHandler extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent event) {
		new ServerManager(event.getJDA());
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Server server = ServerManager.getServer(event.getGuild().getIdLong());
		
		server.processMessage(event.getMessage());
		server.updateActivityList(event.getMember());
	}

	@Override
	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
		Member m = event.getMember();
		
		// Passes online status if a player goes offline
		if (m.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
			ServerManager.getServer(event.getGuild().getIdLong()).playerDisconnect(m);
		}
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		// Adds the new server to the server list
		ServerManager.addNewServer(event.getGuild());
		System.out.println(String.format("Joined server: %s", event.getGuild().getName()));
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		// Removes the server from the server list
		ServerManager.removeServer(event.getGuild().getIdLong());
		System.out.println(String.format("Removed from server: %s", event.getGuild().getName()));
	}

	@Override
	public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
		// Updates activity list with the user
		ServerManager.getServer(event.getGuild().getIdLong()).updateActivityList(event.getMember());
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		// Inserts new player into database
		Database.insertPlayer(event.getUser().getIdLong(), event.getMember().getEffectiveName());
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		Server server = ServerManager.getServer(event.getGuild().getIdLong());
		
		server.getQueueManager().purgeQueue(event.getMember());
	}
	
	@Override
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		if(!event.getUser().isBot()){
			MenuRouter.newReactionEvent(event.getMessageIdLong(), event.getReactionEmote().getName());
		}
	}

	@Override
	public void onPrivateMessageReactionRemove(PrivateMessageReactionRemoveEvent event){
		if(!event.getUser().isBot()){
			MenuRouter.newReactionEvent(event.getMessageIdLong(), event.getReactionEmote().getName());
		}
	}
}
