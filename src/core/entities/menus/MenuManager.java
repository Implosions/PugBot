package core.entities.menus;

import net.dv8tion.jda.core.entities.Member;

public abstract class MenuManager<Controller extends MenuController<?>, Menu extends EmbedMenu> {

	protected Member owner;
	protected Menu menu;
	protected Controller controller;

	public MenuManager(Member owner, Controller controller) {
		this.owner = owner;
		this.controller = controller;
	}

	public void complete() {
		menu.complete();
	}

	public void cancel() {
		menu.delete();
	}

	public Member getOwner() {
		return owner;
	}

	public Member getOpponentOwner() {
		return controller.getOpponent(this).getOwner();
	}

	public Menu getMenu() {
		return menu;
	}

	public MenuOptions getOptions() {
		return controller.getOptions();
	}

	protected abstract void createMenu();

	protected abstract <T> void menuAction(int pageIndex, T action);

}
