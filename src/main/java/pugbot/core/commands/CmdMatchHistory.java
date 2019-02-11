package pugbot.core.commands;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.Database;
import pugbot.core.entities.MatchRecord;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class CmdMatchHistory extends Command {
	
	private static int recordLimit = 5;	
	private static String recordFormat = "%-8s%-15s%-30s%-30s%-10s";
	private static String recordSecondRowOffset = new String(new char[53]).replace('\0', ' ');
	private static String rowSeparator = new String(new char[95]).replace('\0', '_');
	private static String columnTitles = String.format(recordFormat, "Index", "Date", "Queue", "Captains", "Winning Team");
	
	private List<MatchRecord> recordCache;
	
	@Override
	public Message execCommand(Member caller, String[] args) {		
		if(args.length == 0) {
			recordCache = Database.queryGetServerMatchRecords(server.getId(), recordLimit);
			return generateMatchHistoryMessage();
		}
		
		if(args.length < 2) {
			throw new BadArgumentsException();
		}
		
		if(recordCache == null) {
			recordCache = Database.queryGetServerMatchRecords(server.getId(), recordLimit);
		}
		
		int recordIndex;
		
		try {
			recordIndex = Integer.valueOf(args[0]);
		} catch (NumberFormatException ex) {
			throw new BadArgumentsException("The row index must be a valid integer");
		}
		
		if(recordIndex < 1 || recordIndex > recordLimit) {
			throw new InvalidUseException(
					String.format("The row index must be greater than 0 and less than %d", recordLimit));
		}
		
		MatchRecord record = recordCache.get(recordIndex - 1);
		String resultString = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
		Integer resultInteger = null;
		
		if(resultString.equals("tie")) {
			resultInteger = 0;
		} else if(resultString.equals("cancelled")) {
			resultInteger = null;
		} else {
			if(!record.hasCaptainWithName(resultString)) {
				throw new InvalidUseException(String.format("The name '%s' did not match either captain", resultString));
			}
			
			for(int i = 0; i < 2; i++) {
				if(record.captainNames[i].toLowerCase().equals(resultString)) {
					resultInteger = i + 1;
					break;
				}
			}
		}
		
		if(resultInteger == record.getResult()) {
			throw new InvalidUseException("The new result must be different");
		}
		
		record.updateResult(resultInteger);
		Database.updateGameResult(record.serverId, record.queueId, record.timestamp, record.getResult());
		
		return Utils.createMessage("`Match record updated`");
	}
	
	private Message generateMatchHistoryMessage() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("```\n");
		sb.append(columnTitles);
		sb.append('\n');
		sb.append(rowSeparator);
		sb.append('\n');
		sb.append('\n');
		
		for(int i = 0; i < recordCache.size(); i++) {
			MatchRecord record = recordCache.get(i);
			String date = String.format("%tF", new Date(record.timestamp));
			String[] captains = record.captainNames;
			String translatedResult = new String();
			
			if(record.getResult() == null) {
				translatedResult = "Cancelled";
			} else if(record.getResult() == 0) {
				translatedResult = "Tie";
			} else {
				translatedResult = captains[record.getResult() - 1];
			}
			
			String row = String.format(recordFormat, 
					i + 1,
					date,
					record.queueName,
					captains[0],
					translatedResult);
			
			sb.append(row);
			sb.append('\n');
			sb.append(recordSecondRowOffset);
			sb.append(captains[1]);
			sb.append('\n');
			sb.append(rowSeparator);
			sb.append('\n');
			sb.append('\n');
		}
		
		sb.append("```");
		
		return Utils.createMessage(sb.toString());
	}
	
	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return false;
	}

	@Override
	public String getName() {
		return "MatchHistory";
	}

	@Override
	public String getDescription() {
		return "Lists past games with the ability to amend results";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " - Lists past matches and their results\n"
				+ getBaseCommand() + " <record index> <new result (captain name/tie/cancelled)> - Updates a match record";
	}
	
}
