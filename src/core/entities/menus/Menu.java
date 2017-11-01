package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.util.Trigger;

public abstract class Menu {
	protected List<MenuItem> menuItemList = new ArrayList<MenuItem>();
	protected Trigger trigger;
	
	protected abstract void complete();
	protected abstract void createMenuItems();
}
