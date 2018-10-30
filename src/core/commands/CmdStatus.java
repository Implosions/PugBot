package core.commands;

import java.util.List;

import core.entities.Game;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdStatus extends Command {

	public CmdStatus(Server server) {
		super(server);
	}

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
		String statusMsg = "";

		for (Queue q : queueList) {
			// Get basic queue information
			statusMsg += String.format("**%s** [%s/%s]%n", q.getName(), q.getCurrentPlayersCount(), q.getMaxPlayers());
			
			// Get players in queue
			if (q.getCurrentPlayersCount() > 0) {
				String names = ""; 
				for (Member m : q.getPlayersInQueue()) {
					names += m.getEffectiveName() + ", ";
				}
				names = names.substring(0, names.lastIndexOf(","));
				statusMsg += String.format("**IN QUEUE**: %s%n", names);
			}
			
			// Get players in game
			if (q.getNumberOfGames() > 0) {
				for (Game g : q.getGames()) {
					String names = "";
					for (Member m : g.getPlayers()) {
						names += m.getEffectiveName() + ", ";
					}
					names = names.substring(0, names.lastIndexOf(","));
					statusMsg += String.format("**IN GAME**: %s @ %d minutes ago%n", names, (System.currentTimeMillis() - g.getTimestamp()) / 60000);
				}
			}
			statusMsg += System.lineSeparator();
		}
		
		if (statusMsg.isEmpty()) {
			throw new InvalidUseException("Queue does not exist");
		}
		
		return statusMsg;
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
