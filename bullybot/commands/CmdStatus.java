package bullybot.commands;

import java.util.ArrayList;

import bullybot.classfiles.Queue;
import bullybot.classfiles.Game;
import bullybot.classfiles.Info;
import bullybot.classfiles.QueueManager;
import bullybot.classfiles.util.Functions;
import bullybot.errors.DoesNotExistException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class CmdStatus extends Command {
	
	public CmdStatus() {
		this.helpMsg = Info.STATUS_HELP;
		this.description = Info.STATUS_DESC;
		this.name = "status";
	}

	@Override
	public void execCommand(QueueManager qm, Member member, ArrayList<String> args) {
		try {
			if (!qm.isQueueListEmpty()) {
				if (args.isEmpty()) {
					this.response = Functions.createMessage("", statusBuilder(qm.getQueue()), true);
				} else {
					ArrayList<Queue> queueList = new ArrayList<Queue>();
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
					this.response = Functions.createMessage("", statusBuilder(queueList), true);
				}
			} else {
				throw new DoesNotExistException("Queue");
			}
			if(lastResponseId != null){
				qm.getServer().getPugChannel().deleteMessageById(lastResponseId).complete();
			}
			System.out.println("Completed status request");
		} catch (DoesNotExistException ex) {
			this.response = Functions.createMessage("Error!", ex.getMessage(), false);
		}
	}

	private String statusBuilder(ArrayList<Queue> queueList) {
		String statusMsg = "";

		for (Queue q : queueList) {
			statusMsg += String.format("**%s** [%s/%s]%n", q.getName(), q.getCurrentPlayers(), q.getMaxPlayers());
			if (q.getCurrentPlayers() > 0) {
				String names = ""; 
				for (User u : q.getPlayersInQueue()) {
					names += u.getName() + ", ";
				}
				names = names.substring(0, names.lastIndexOf(","));
				statusMsg += String.format("**IN QUEUE**: %s%n", names);
			}
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
