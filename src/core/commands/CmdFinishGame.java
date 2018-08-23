package core.commands;

import java.util.concurrent.TimeUnit;

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

public class CmdFinishGame extends Command {

	public CmdFinishGame(Server server) {
		this.helpMsg = Constants.FINISHGAME_HELP;
		this.description = Constants.FINISHGAME_DESC;
		this.name = Constants.FINISHGAME_NAME;
		this.pugChannelOnlyCommand = true;
		this.server = server;
	}
	
	@Override
	public Message execCommand(Member caller, String[] args) {
		QueueManager qm = server.getQueueManager();
		
		if(qm.isPlayerWaiting(caller)){
			return null;
		}
		
		Game game = qm.getPlayersGame(caller);
		
		if(game == null){
			throw new InvalidUseException("You are not currently in-game");
		}
		
		if(!(caller.equals(game.getCaptain1()) || caller.equals(game.getCaptain2()))){
			throw new InvalidUseException("You must be a captain to finish your game");
		}
		
		String title = String.format("Game '%s' finished", game.getQueueName());
		
		if(game.getStatus() == GameStatus.PICKING){
			qm.finishGame(game, null);
			
			return Utils.createMessage("`" + title + "`");
		}
		
		if(args.length == 0){
			throw new BadArgumentsException();
		}
		
		String result = args[0].toLowerCase();
		int team = caller.equals(game.getCaptain1()) ? 1 : 2;
		int winningTeam;
		
		switch(result){
		case "tie": winningTeam = 0; break;
		case "win": winningTeam = team; break;
		case "loss": winningTeam = (team == 1) ? 2 : 1; break;
		default: throw new BadArgumentsException("Result must either be `win`, `loss`, or `tie`");
		}
		
		qm.finishGame(game, winningTeam);
		
		long msDiff = System.currentTimeMillis() - game.getTimestamp();
		long duration = TimeUnit.MINUTES.convert(msDiff, TimeUnit.MILLISECONDS);
		
		if(winningTeam != 0){
			Member winner = (winningTeam == 1) ? game.getCaptain1() : game.getCaptain2();
			String teamName = "Team " + winner.getEffectiveName();
			
			response = Utils.createMessage(title, String.format("**Winner:** %s%n**Duration:** %d Minutes",
					teamName, duration), true);
		}else{
			response = Utils.createMessage(title, String.format("**Tie game**%n**Duration:** %d Minutes", duration), true);
		}
		
		return response;
	}

}
