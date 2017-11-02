package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.util.Trigger;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public abstract class Menu extends ListenerAdapter{
	protected List<MenuItem> menuItemList = new ArrayList<MenuItem>();
	protected Trigger trigger;
	
	protected abstract void complete();
	protected abstract void createMenuItems();
	protected MenuItem getMenuItem(String id){
		for(MenuItem m : menuItemList){
			if (m.getId().equals(id)){
				return m;
			}
		}
		return null;
	}
}
