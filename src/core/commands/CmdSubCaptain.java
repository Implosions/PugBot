package core.commands;

import core.Constants;
import core.entities.Game;
import core.entities.Game.GameStatus;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class CmdSubCaptain extends Command {

	public CmdSubCaptain(Server server) {
		this.name = Constants.SUBCAPTAIN_NAME;
		this.description = Constants.SUBCAPTAIN_DESC;
		this.helpMsg = Constants.SUBCAPTAIN_HELP;
		this.pugChannelOnlyCommand = true;
		this.server = server;
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		QueueManager qm = server.getQueueManager();
		Member targetMember = server.getMember(args[0]);

		User target = targetMember.getUser();
		User sub = caller.getUser();

		if (!qm.isPlayerIngame(sub)) {
			throw new InvalidUseException("You are not in-game");
		}

		if (qm.isPlayerIngame(target)) {
			throw new InvalidUseException(targetMember.getEffectiveName() + " is not in-game");
		}

		Game game = qm.getPlayersGame(sub);

		if (game.getStatus() != GameStatus.PICKING) {
			throw new InvalidUseException("Picking has finished");
		}

		if (game.getCaptains()[0] == target || game.getCaptains()[1] == target) {
			throw new InvalidUseException(targetMember.getEffectiveName() + " is not a captain in your game");
		}

		if (game.getCaptains()[0] != sub && game.getCaptains()[1] != sub) {
			throw new InvalidUseException("You are already a captain");
		}

		game.subCaptain(sub, target);

		this.response = Utils.createMessage(String.format("`%s has replaced %s as captain`", caller.getEffectiveName(),
				targetMember.getEffectiveName()));

		return response;
	}
}
