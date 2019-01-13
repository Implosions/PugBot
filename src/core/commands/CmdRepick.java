package core.commands;

import java.util.ArrayList;
import java.util.List;

import core.entities.Game;
import core.entities.PUGTeam;
import core.entities.menus.ConfirmationMenu;
import core.exceptions.InvalidUseException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdRepick extends Command {

	private List<Long> gameList = new ArrayList<Long>();
	
	@Override
	public Message execCommand(Member caller, String[] args) {
		Game game = server.getQueueManager().getPlayersGame(caller);
		
		if(game == null){
			throw new InvalidUseException("You are not in-game");
		}
		
		if(!game.isCaptain(caller)){
			throw new InvalidUseException("You must be a captain to use this command");
		}
		
		if(!gameList.contains(game.getTimestamp())){
			gameList.add(game.getTimestamp());
			
			new Thread(new Runnable(){
				public void run(){
					Member captain2;
					PUGTeam[] teams = game.getPUGTeams();
					
					if(teams[0].getCaptain() == caller) {
						captain2 = teams[1].getCaptain();
					} else {
						captain2 = teams[0].getCaptain();
					}
					
					String title = "Your opponent has requested to repick the teams";
					ConfirmationMenu menu = new ConfirmationMenu(captain2, title);
					menu.start();
					
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
