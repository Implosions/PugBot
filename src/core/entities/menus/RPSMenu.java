package core.entities.menus;

import java.awt.Color;

import core.Constants;
import core.entities.menus.RPSMenuController.RPSMove;
import net.dv8tion.jda.core.entities.MessageChannel;

public class RPSMenu extends EmbedMenu{
	
	public RPSMenu(MessageChannel channel, RPSTeam manager) {
		super(channel, manager);
		
		embedBuilder.setDescription("Make a move!")
					.setColor(Color.green)
					.setTitle(String.format("%sRock %sPaper %sScissors%nvs. %s",
							Constants.Emoji.PUNCH,
							Constants.Emoji.RAISED_HAND,
							Constants.Emoji.V,
							manager.getOpponentOwner().getEffectiveName()));
		
		register();
	}

	@Override
	public void fieldButtonClick(int index) {
	}

	@Override
	public void utilityButtonClick(String emoteName) {
		RPSMove move = null;
		
		switch(emoteName){
		case Constants.Emoji.PUNCH: move = RPSMove.ROCK; break;
		
		case Constants.Emoji.RAISED_HAND: move = RPSMove.PAPER; break;
		
		case Constants.Emoji.V: move = RPSMove.SCISSORS; break;
		
		case Constants.Emoji.STOP_SIGN: move = RPSMove.FORFEIT; break;
		}
		
		manager.menuAction(0, move);
	}
	
	public void updateWaiting() {
		embedBuilder.setColor(Color.yellow)
					.setDescription("Waiting for opponent...");		
		update();
	}
	
	public void updateWin() {
		embedBuilder.setColor(Color.green)
					.setDescription("You have won!");
		update();
	}
	
	public void updateLoss() {
		embedBuilder.setColor(Color.red)
					.setDescription("You have lost");
		update();
	}
	
	public void updateTie() {
		embedBuilder.setColor(Color.yellow)
					.setDescription("You have tied with your opponent");
		update();
	}
	
	public void updateForfeit() {
		embedBuilder.setColor(Color.red)
					.setDescription("You have forfeited");
		update();
	}
}
