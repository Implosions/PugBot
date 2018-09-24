package core.commands;

import core.entities.Game;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdSub extends Command {

	public CmdSub(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();

		if (args.length > 2 || args.length == 0) {
			throw new BadArgumentsException();
		}

		Member target = server.getMember(args[0]);
		Member substitute;

		if (args.length == 1) {
			substitute = caller;
		} else {
			if(!server.isAdmin(caller)){
				throw new InvalidUseException("Admin required to force-substitute a player");
			}
			
			substitute = server.getMember(args[1]);
		}

		if (qm.isPlayerIngame(substitute)) {
			throw new InvalidUseException(substitute.getEffectiveName() + " is already in-game");
		}

		if (!qm.isPlayerIngame(target)) {
			throw new InvalidUseException(target.getEffectiveName() + " is not in-game");
		}

		Game game = qm.getPlayersGame(target);

		game.sub(target, substitute);
		qm.purgeQueue(substitute);

		this.response = Utils.createMessage(String.format("`%s has been substituted with %s`",
				target.getEffectiveName(), substitute.getEffectiveName()));

		qm.updateTopic();

		return response;
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
		return "Sub";
	}

	@Override
	public String getDescription() {
		return "Substites a player currently in-game";
	}

	@Override
	public String getHelp() {
		return  getBaseCommand() + " <username> - Substitutes a player in-game for yourself\n" +
				getBaseCommand() + " <target username> <substitute username> - Substitues a player for another (Admin required)";
	}
}
