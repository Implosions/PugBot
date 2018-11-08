package core.entities.menus;

import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

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

	public int getNumberOfPagesInMenu() {
		return controller.getNumberOfPages();
	}

	public List<Field> getPage(int index) {
		return controller.getPage(index);
	}

	protected abstract void createMenu();

	protected abstract <T> void menuAction(T action);

}
