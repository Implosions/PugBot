package core.entities.menus;

import core.Constants;
import core.Database;
import core.entities.RPSMove;
import net.dv8tion.jda.core.entities.Member;

public class RPSMenuController extends MenuController<RPSMenu> {

	private int winner = -1;

	public RPSMenuController(Member p1, Member p2) {
		RPSMenu[] menus = new RPSMenu[2];
		menus[0] = new RPSMenu(p1, this);
		menus[1] = new RPSMenu(p2, this);
		setMenus(menus);
		
		String title = String.format("%sRock %sPaper %sScissors%nvs. ",
				Constants.Emoji.PUNCH,
				Constants.Emoji.RAISED_HAND,
				Constants.Emoji.V);
		
		getMenu(0).setTitle(title + p2.getEffectiveName());
		getMenu(1).setTitle(title + p1.getEffectiveName());
	}

	public Member getWinner() {
		return getMenu(winner).getOwner();
	}

	public synchronized void menuActionTaken(IMenu sender) {
		if(getMenu(0).getMove() == null || getMenu(1).getMove() == null) {
			((RPSMenu)sender).updateWaiting();
		}
		
		notifyAll();
	}

	@Override
	protected boolean checkCondition() {
		RPSMove p1Move = getMenu(0).getMove();
		RPSMove p2Move = getMenu(1).getMove();
		
		if (p1Move == RPSMove.FORFEIT) {
			winner = 1;
		} else if (p2Move == RPSMove.FORFEIT) {
			winner = 0;
		} else if (p1Move != null && p2Move != null) {
			winner = RPSMove.getWinner(p1Move, p2Move);

			if (winner == -1) {
				getMenu(0).resetMove();
				getMenu(1).resetMove();

				getMenu(0).updateTie();
				getMenu(1).updateTie();
			}
		}

		return winner == -1;
	}

	@Override
	protected void complete() {
		Long timestamp = System.currentTimeMillis();
		RPSMenu winningSide = getMenu(winner);
		RPSMenu losingSide = getMenu((winner == 0) ? 1 : 0);
		
		winningSide.updateWin();
		losingSide.updateLoss();

		getMenu(0).complete();
		getMenu(1).complete();
		
		Database.insertRPSGame(timestamp, getWinner().getUser().getIdLong(), 1);
		Database.insertRPSGame(timestamp, losingSide.getOwner().getUser().getIdLong(), 0);
	}

	@Override
	protected MenuOptions buildMenuOptions() {
		MenuOptions options = new MenuOptions(0);
		
		options.addUtilityButton(Constants.Emoji.PUNCH);
		options.addUtilityButton(Constants.Emoji.RAISED_HAND);
		options.addUtilityButton(Constants.Emoji.V);
		options.addUtilityButton(Constants.Emoji.STOP_SIGN);
		
		return options;
	}
}
