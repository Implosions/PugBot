package core.entities.menus;

import core.entities.menus.RPSMenuController.RPSMove;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class RPSTeam extends MenuManager<RPSMenuController, RPSMenu> {

	private RPSMove move = null;

	public RPSTeam(Member owner, RPSMenuController controller) {
		super(owner, controller);
	}

	@Override
	protected RPSMenu createMenu() {
		MessageChannel channel = owner.getUser().openPrivateChannel().complete();

		return new RPSMenu(channel, this);
	}

	@Override
	protected void menuAction(Object action) {
		RPSMove move = (RPSMove) action;

		this.move = move;
		controller.managerActionTaken(this);
	}

	public RPSMove getMove() {
		return move;
	}

	public void resetMove() {
		move = null;
	}
}
