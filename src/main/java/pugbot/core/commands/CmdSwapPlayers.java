package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.Game;
import pugbot.core.entities.QueueManager;
import pugbot.core.entities.Game.GameStatus;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

public class CmdSwapPlayers extends Command {

	@Override
	public Message execCommand(Member caller, String[] args) {
		if(args.length != 2){
			throw new BadArgumentsException();
		}
		
		QueueManager qm = server.getQueueManager();
		Member p1 = server.getMember(args[0]);
		Member p2 = server.getMember(args[1]);
		Game game = qm.getPlayersGame(p1);
		
		if(game == null){
			throw new InvalidUseException(String.format("%s is not in-game", p1.getEffectiveName()));
		}
		
		if(!(game.isCaptain(caller) || server.isAdmin(caller))){
			throw new InvalidUseException("You must be a captain or admin to use this command");
		}
		
		if(game.getStatus() != GameStatus.PLAYING){
			throw new InvalidUseException("Game must not be currently picking or finished");
		}
		
		if(!game.containsPlayer(p2)){
			throw new InvalidUseException("Players are not in the same game");
		}
		
		if(game.isCaptain(p1) || game.isCaptain(p2)){
			throw new InvalidUseException("Captains cannot be swapped");
		}
		
		if(game.getTeam(p1) == game.getTeam(p2)){
			throw new InvalidUseException("Players must be on different teams");
		}
		
		game.swapPlayers(p1, p2);
		
		return Utils.createMessage(
				String.format("`%s has been swapped with %s`", p1.getEffectiveName(), p2.getEffectiveName()));
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
		return "SwapPlayers";
	}

	@Override
	public String getDescription() {
		return "Swaps a player on each team in a game";
	}

	@Override
	public String getHelp() {
		return getBaseCommand() + " <player1> <player2>";
	}

}
