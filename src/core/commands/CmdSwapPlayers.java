package core.commands;

import core.Database;
import core.entities.Game;
import core.entities.Game.GameStatus;
import core.entities.QueueManager;
import core.entities.Server;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdSwapPlayers extends Command{

	public CmdSwapPlayers(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		if(args.length != 2){
			throw new BadArgumentsException();
		}
		
		QueueManager qm = server.getQueueManager();
		Member p1 = server.getMember(args[0]);
		Member p2 = server.getMember(args[0]);
		Game game = qm.getPlayersGame(caller);
		
		if(game == null){
			throw new InvalidUseException("You are not in-game");
		}
		
		if(!game.isCaptain(caller)){
			throw new InvalidUseException("You must be a captain to use this command");
		}
		
		if(game.getStatus() != GameStatus.PLAYING){
			throw new InvalidUseException("Game must not be currently picking or finished");
		}
		
		if(!qm.isPlayerIngame(p1) || !qm.isPlayerIngame(p2)){
			throw new InvalidUseException("Both players must be in-game");
		}
		
		if(!game.containsPlayer(p1) || !game.containsPlayer(p2)){
			throw new InvalidUseException("Players are not in the same game");
		}
		
		if(game.isCaptain(p1) || game.isCaptain(p2)){
			throw new InvalidUseException("Captains cannot be swapped");
		}
		
		Database.swapPlayers(game.getTimestamp(), p1.getUser().getIdLong(), p2.getUser().getIdLong());
		
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
