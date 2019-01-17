package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.ServerManager;

public class CmdRestart extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		server.getQueueManager().finishAllGames();
		ServerManager.removeServer(server.getId());
		ServerManager.addNewServer(server.getGuild());

		return Utils.createMessage("`Server instance restarted`");
	}

	@Override
	public boolean isAdminRequired() {
		return true;
	}

	@Override
	public boolean isGlobalCommand() {
		return true;
	}

	@Override
	public String getName() {
		return "Restart";
	}

	@Override
	public String getDescription() {
		return "Restarts this bot's instance";
	}

	@Override
	public String getHelp() {
		return getBaseCommand();
	}

}
