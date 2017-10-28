package core.commands;

import java.util.ArrayList;
import java.util.List;

import core.Constants;
import core.entities.Game;
import core.entities.Queue;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.DoesNotExistException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class CmdStatus extends Command {
	
	public CmdStatus() {
		this.helpMsg = Constants.STATUS_HELP;
		this.description = Constants.STATUS_DESC;
		this.name = Constants.STATUS_NAME;
	}

	@Override
	public void execCommand(Server server, Member member, String[] args) {
		QueueManager qm = server.getQueueManager();
		try {
			if (!qm.isQueueListEmpty()) {
				if (args.length == 0) {
					this.response = Utils.createMessage("", statusBuilder(qm.getQueueList()), true);
				} else {
					List<Queue> queueList = new ArrayList<Queue>();
					for (String a : args) {
						Queue queue;
						try {
							queue = qm.getQueue(Integer.valueOf(a));
						} catch (NumberFormatException ex) {
							queue = qm.getQueue(a);
						}
						if (queue != null) {
							queueList.add(queue);
						}
					}
					this.response = Utils.createMessage("", statusBuilder(queueList), true);
				}
			} else {
				throw new DoesNotExistException("Queue");
			}
			// Delete last status message
			if(lastResponseId != null){
				qm.getServer().getPugChannel().deleteMessageById(lastResponseId).complete();
			}
			System.out.println("Completed status request");
		} catch (DoesNotExistException ex) {
			this.response = Utils.createMessage("Error!", ex.getMessage(), false);
		} catch (PermissionException ex){
			lastResponseId = null;
			ex.printStackTrace();
		}
	}

	private String statusBuilder(List<Queue> queueList) {
		String statusMsg = "";

		for (Queue q : queueList) {
			// Get basic queue information
			statusMsg += String.format("**%s** [%s/%s]%n", q.getName(), q.getCurrentPlayers(), q.getMaxPlayers());
			
			// Get players in queue
			if (q.getCurrentPlayers() > 0) {
				String names = ""; 
				for (User u : q.getPlayersInQueue()) {
					names += u.getName() + ", ";
				}
				names = names.substring(0, names.lastIndexOf(","));
				statusMsg += String.format("**IN QUEUE**: %s%n", names);
			}
			
			// Get players in game
			if (q.getNumberOfGames() > 0) {
				for (Game g : q.getGames()) {
					String names = "";
					for (User u : g.getPlayers()) {
						names += u.getName() + ", ";
					}
					names = names.substring(0, names.lastIndexOf(","));
					statusMsg += String.format("**IN GAME**: %s @ %d minutes ago%n", names, (System.currentTimeMillis() - g.getTimestamp()) / 60000);
				}
			}
			statusMsg += System.lineSeparator();
		}
		if (statusMsg.isEmpty()) {
			throw new DoesNotExistException("Queue");
		}
		return statusMsg;
	}
}
