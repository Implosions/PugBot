package core.commands;

import java.awt.Color;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import core.Database;
import core.entities.Queue;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdStats extends Command {
	
	private final static String STATS_COLUMN_FORMAT = "%-12s%-15s%-10s%n";

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

			return messageBuilder.append("`Server Stats`").setEmbed(embedBuilder.build()).build();
		}else{
			Member player = server.getMember(String.join(" ", args));
			long userId = player.getUser().getIdLong();
			
			for(Queue queue : server.getQueueManager().getQueueList()){
				int totalGames = Database.queryGetPlayerTotalCompletedGames(server.getId(), userId, queue.getId());

				if(totalGames == 0){
					continue;
				}
				
				int wins = Database.queryGetPlayerTotalWins(server.getId(), userId, queue.getId());
				int losses = Database.queryGetPlayerTotalLosses(server.getId(), userId, queue.getId());
				int ties = totalGames - (wins + losses);
				int avgPickPosition = Database.queryGetPlayerAvgPickPosition(server.getId(), userId, queue.getId());
				int captainWinPercent = Database.queryGetPlayerCaptainWinPercent(server.getId(), userId, queue.getId());
				Date lastPlayedDate = Database.queryGetPlayerLastPlayedDate(server.getId(), userId, queue.getId());
				
				StringBuilder tableBuilder = new StringBuilder();
				String wlt = String.format("%d/%d/%d", wins, losses, ties);
				String dateString = String.format("%tD", lastPlayedDate);
				
				tableBuilder.append("```css\n");
				tableBuilder.append(String.format(STATS_COLUMN_FORMAT, "Games", "Win/Loss/Tie", "Last played"));
				tableBuilder.append(String.format(STATS_COLUMN_FORMAT, totalGames, wlt, dateString));
				tableBuilder.append(System.lineSeparator());
				tableBuilder.append(String.format(STATS_COLUMN_FORMAT, "Avg. Pick", "Captain Win %", ""));
				tableBuilder.append(String.format(STATS_COLUMN_FORMAT, avgPickPosition, captainWinPercent, ""));
				tableBuilder.append("```");
				
				String table = tableBuilder.toString();
				
				embedBuilder.addField(queue.getName(), table, false);
			}
			
			if(embedBuilder.getFields().size() == 0){
				embedBuilder.appendDescription("N/A");
			}else{
				embedBuilder.setFooter(new String(new char[100]).replace('\0', '-'), null);
			}
			
			return messageBuilder.append(String.format("`%s's stats`", player.getEffectiveName())).setEmbed(embedBuilder.build()).build();
		}
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "Stats";
	}

	@Override
	public String getDescription() {
		return "Retrieves server or player stats";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Lists general server stats\n" +
				getBaseCommand() + " <username> - Lists a player's stats";
	}

}
