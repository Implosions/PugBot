package core.commands;

import core.entities.Game;
import core.entities.QueueManager;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdSubCaptain extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if (args.length != 1) {
			throw new BadArgumentsException();
		}

		QueueManager qm = server.getQueueManager();
		Member target = server.getMember(args[0]);

		if (!qm.isPlayerIngame(caller)) {
			throw new InvalidUseException("You are not in-game");
		}

		Game game = qm.getPlayersGame(caller);

		if (!game.isCaptain(target)) {
			throw new InvalidUseException(target.getEffectiveName() + " is not a captain in your game");
		}

		if (game.isCaptain(caller)) {
			throw new InvalidUseException("You are already a captain");
		}

		game.subCaptain(caller, target);

		return Utils.createMessage(String.format("`%s has replaced %s as captain`", caller.getEffectiveName(),
				target.getEffectiveName()));
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
		return "SubCaptain";
	}

	@Override
	public String getDescription() {
		return "Substitute a captain in an ongoing game";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <captain username>";
	}
}
