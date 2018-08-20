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
	protected void createMenu() {
		MessageChannel channel = owner.getUser().openPrivateChannel().complete();

		menu = new RPSMenu(channel, this);
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
