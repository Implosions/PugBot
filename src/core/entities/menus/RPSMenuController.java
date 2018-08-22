package core.entities.menus;

import java.util.Arrays;

import core.Constants;
import core.Database;
import net.dv8tion.jda.core.entities.Member;

public class RPSMenuController extends MenuController<RPSTeam> {

	private Member winner = null;

	public enum RPSMove {
		ROCK,
		PAPER,
		SCISSORS,
		FORFEIT
	}

	public RPSMenuController(Member p1, Member p2) {
		utilityButtons = Arrays.asList(Constants.Emoji.PUNCH, Constants.Emoji.RAISED_HAND, Constants.Emoji.V,
				Constants.Emoji.STOP_SIGN);

		manager1 = new RPSTeam(p1, this);
		manager2 = new RPSTeam(p2, this);
	}

	public Member getWinner() {
		return winner;
	}

	@Override
	public synchronized void managerActionTaken(RPSTeam manager) {
		if (getOpponent(manager).getMove() == null) {
			manager.getMenu().updateWaiting();
		}

		notifyAll();
	}

	@Override
	protected boolean checkCondition() {
		if (manager1.getMove() == RPSMove.FORFEIT) {
			winner = manager2.getOwner();
		} else if (manager2.getMove() == RPSMove.FORFEIT) {
			winner = manager1.getOwner();
		} else if (manager1.getMove() != null && manager2.getMove() != null) {
			winner = determineWinner(manager1, manager2);

			if (winner == null) {
				manager1.resetMove();
				manager2.resetMove();

				manager1.getMenu().updateTie();
				manager2.getMenu().updateTie();
			}
		}

		return winner == null;
	}

	@Override
	protected void complete() {
		Long timestamp = System.currentTimeMillis();
		Member loser;
		
		if (winner == manager1.getOwner()) {
			manager1.getMenu().updateWin();

			if (manager2.getMove() == RPSMove.FORFEIT) {
				manager2.getMenu().updateForfeit();
			} else {
				manager2.getMenu().updateLoss();
			}
			
			loser = manager2.getOwner();
		} else {
			manager2.getMenu().updateWin();

			if (manager1.getMove() == RPSMove.FORFEIT) {
				manager1.getMenu().updateForfeit();
			} else {
				manager1.getMenu().updateLoss();
			}
			
			loser = manager1.getOwner();
		}

		manager1.complete();
		manager2.complete();
		
		Database.insertRPSGame(timestamp, winner.getUser().getIdLong(), 1);
		Database.insertRPSGame(timestamp, loser.getUser().getIdLong(), 0);
	}

	public Member determineWinner(RPSTeam t1, RPSTeam t2) {
		if (t1.getMove() == t2.getMove()) {
			return null;
		} else if (t1.getMove() == RPSMove.ROCK) {
			return t2.getMove() == RPSMove.SCISSORS ? t1.getOwner() : t2.getOwner();
		} else if (t1.getMove() == RPSMove.PAPER) {
			return t2.getMove() == RPSMove.ROCK ? t1.getOwner() : t2.getOwner();
		} else {
			return t2.getMove() == RPSMove.PAPER ? t1.getOwner() : t2.getOwner();
		}
	}

	@Override
	protected void generatePages() {
		pages = null;
	}
	
	@Override
	protected void onMenuCreation() {
	}
}
