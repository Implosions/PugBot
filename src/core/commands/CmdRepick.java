package core.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.entities.Game;
import core.entities.Game.GameStatus;
import core.entities.Server;
import core.entities.menus.RepickConfirmationMenu;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRepick extends Command {

	private static List<Long> gameList = Collections.synchronizedList(new ArrayList<Long>());
	
	public CmdRepick(Server server) {
		super(server);
	}

	@Override
	public Message execCommand(Member caller, String[] args) {
		Game game = server.getQueueManager().getPlayersGame(caller);
		
		if(game == null){
			throw new InvalidUseException("You are not in-game");
		}
		
		if(!game.isCaptain(caller)){
			throw new InvalidUseException("You must be a captain to use this command");
		}
		
		if(game.getStatus() != GameStatus.PLAYING){
			throw new InvalidUseException("You must finish picking before calling for a repick");
		}
		
		if(!gameList.contains(game.getTimestamp())){
			gameList.add(game.getTimestamp());
			
			new Thread(new Runnable(){
				public void run(){
					Member captain = (game.getCaptain1() == caller) ? game.getCaptain2() : game.getCaptain1();
					RepickConfirmationMenu menu = new RepickConfirmationMenu(captain);
					
					if(menu.getResult()){
						game.repick();
					}
					
					gameList.remove(game.getTimestamp());
				}
			}).start();
		}
		
		return null;
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
		return "Repick";
	}

	@Override
	public String getDescription() {
		return "Restarts the picking process for your game";
	}

	@Override
	public String getHelp() {
		return getBaseCommand();
	}

}
