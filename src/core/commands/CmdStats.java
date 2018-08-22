package core.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import core.Constants;
import core.Database;
import core.entities.Server;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdStats extends Command {

	public CmdStats(Server server){
		this.name = Constants.STATS_NAME;
		this.description = Constants.STATS_DESC;
		this.helpMsg = Constants.STATS_HELP;
		this.server = server;
	}
	
	@Override
	public Message execCommand(Member caller, String[] args) {
		EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.green);
		MessageBuilder messageBuilder = new MessageBuilder();
		
		if(args.length == 0){
			long timeCutoff = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
			int totalGames = Database.queryGetServerTotalGames(server.getId(), 0);
			int totalGamesInAWeek = Database.queryGetServerTotalGames(server.getId(), timeCutoff);
			int totalPlayers = Database.queryGetServerUniquePlayerCount(server.getId(), 0);
			int totalPlayersInAWeek = Database.queryGetServerUniquePlayerCount(server.getId(), timeCutoff);
			
			embedBuilder.addField("Total Games", String.valueOf(totalGames), true)
						.addField("Unique Players", String.valueOf(totalPlayers), true)
						.addField("\u200b", "\u200b", false)
						.addField("Last 7 Days", String.valueOf(totalGamesInAWeek), true)
						.addField("Last 7 Days", String.valueOf(totalPlayersInAWeek), true);

			response = messageBuilder.append("`Server Stats`").setEmbed(embedBuilder.build()).build();
		}else{
			Member player = server.getMember(String.join(" ", args));
			
			int completedGames = Database.queryGetPlayerTotalCompletedGames(server.getId(), player.getUser().getIdLong());
			int wins = Database.queryGetPlayerTotalWins(server.getId(), player.getUser().getIdLong());
			int losses = Database.queryGetPlayerTotalLosses(server.getId(), player.getUser().getIdLong());
			int ties = completedGames - wins - losses;
			int avgPickPosition = Database.queryGetPlayerAvgPickPosition(server.getId(), player.getUser().getIdLong());
			
			embedBuilder.addField("Games Played", String.valueOf(completedGames), true)
						.addField("Win/Loss/Tie", String.format("%d/%d/%d", wins, losses, ties), true)
						.addField("avg. Pick", String.valueOf(avgPickPosition), true);
			
			response = messageBuilder.append(String.format("`%s's stats`", player.getEffectiveName())).setEmbed(embedBuilder.build()).build();
		}
				
		return response;
	}

}
