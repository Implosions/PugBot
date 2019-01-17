package pugbot.core.commands;

import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.Game;
import pugbot.core.entities.PUGTeam;
import pugbot.core.entities.Queue;
import pugbot.core.entities.QueueManager;
import pugbot.core.entities.Game.GameStatus;
import pugbot.core.exceptions.InvalidUseException;

public class CmdStatus extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();
		String statusMsg;
		
		if (args.length == 0) {
			statusMsg = statusBuilder(qm.getQueueList());
		}
		else {
			statusMsg = statusBuilder(qm.getListOfQueuesFromStringArgs(args));
		}
		
		return Utils.createMessage(null, statusMsg, true);
	}

	private String statusBuilder(List<Queue> queueList) {
		if(queueList.size() == 0){
			throw new InvalidUseException("No valid queues");
		}
		
		StringBuilder status = new StringBuilder();

		for (Queue q : queueList) {
			// Get basic queue information
			status.append(String.format("**%s** [%s/%s]%n", q.getName(), q.getCurrentPlayersCount(), q.getMaxPlayers()));
			
			// Get players in queue
			if (q.getCurrentPlayersCount() > 0) {
				status.append("**IN QUEUE**: ");
				
				for (Member m : q.getPlayersInQueue()) {
					status.append(m.getEffectiveName() + ", ");
				}
				
				status.delete(status.length() - 2, status.length());
				status.append("\n");
			}
			
			// Get players in game
			if (q.getNumberOfGames() > 0) {
				for (Game g : q.getGames()) {
					status.append("**IN GAME**:");
					
					if(g.getStatus() == GameStatus.PICKING) {
						for (Member m : g.getPlayers()) {
							status.append(m.getEffectiveName() + ", ");
						}
						
						status.delete(status.length() - 2, status.length());
					} else {
						PUGTeam[] teams = g.getPUGTeams();
						
						status.append("\n");
						status.append(teams[0].toString());
						status.append("\n");
						status.append(teams[1].toString());
					}
					
					status.append("\n");
					
					long time = (System.currentTimeMillis() - g.getTimestamp()) / 60000;
					status.append(String.format("**@ %d minutes ago**", time));
					status.append("\n");
				}
			}
			
			status.append("\n");
		}
		
		return status.toString();
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
		return "Status";
	}

	@Override
	public String getDescription() {
		return "Lists all players in queue and all active games";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " - Lists information for all queues\n" + 
				getBaseCommand() + " <queue name|queue index> - Lists information for a specific queue";
	}
}
