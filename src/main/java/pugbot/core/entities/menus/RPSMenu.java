package pugbot.core.entities.menus;

import java.awt.Color;

import net.dv8tion.jda.core.entities.Member;
import pugbot.core.Constants;
import pugbot.core.entities.RPSMove;

public class RPSMenu extends EmbedMenu {
	
	private RPSMove move;
	private Member owner;
	
	public RPSMenu(Member member, RPSMenuController controller) {
		super(member.getUser().openPrivateChannel().complete(), controller);
		
		owner = member;
		
		getEmbedBuilder().setDescription("Make a move!")
						 .setColor(Color.green);
	}

	@Override
	public void fieldButtonClick(int index) {
	}

	@Override
	public void utilityButtonClick(String emoteName) {
		switch(emoteName){
		case Constants.Emoji.PUNCH: move = RPSMove.ROCK; break;
		
		case Constants.Emoji.RAISED_HAND: move = RPSMove.PAPER; break;
		
		case Constants.Emoji.V: move = RPSMove.SCISSORS; break;
		
		case Constants.Emoji.STOP_SIGN: move = RPSMove.FORFEIT; break;
		}
		
		getController().menuActionTaken(this);
	}
	
	public void updateWaiting() {
		getEmbedBuilder().setColor(Color.yellow)
					.setDescription("Waiting for opponent...");		
		update();
	}
	
	public void updateWin() {
		getEmbedBuilder().setColor(Color.green)
						 .setDescription("You have won!");
		update();
	}
	
	public void updateLoss() {
		String msg = "You have lost";
		
		if(move == RPSMove.FORFEIT) {
			msg = "You have forfeited";
		}
		
		getEmbedBuilder().setColor(Color.red)
						 .setDescription(msg);
		update();
	}
	
	public void updateTie() {
		getEmbedBuilder().setColor(Color.yellow)
						 .setDescription("You have tied with your opponent");
		update();
	}
	
	public RPSMove getMove() {
		return move;
	}
	
	public Member getOwner() {
		return owner;
	}
	
	public void resetMove() {
		move = null;
	}
}
