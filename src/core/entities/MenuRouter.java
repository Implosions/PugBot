package core.entities;

import java.util.HashMap;

import core.entities.menus.IMenu;

public class MenuRouter {

	private static HashMap<Long, IMenu> menuMap = new HashMap<Long, IMenu>();

	public static void register(IMenu menu) {
		menuMap.put(menu.getId(), menu);
	}

	public static void unregister(long menuId) {
		menuMap.remove(menuId);
	}

	public static void newReactionEvent(long messageId, String emojiName) {
		if (menuMap.containsKey(messageId)) {
			menuMap.get(messageId).buttonClick(emojiName);
		}
	}
}
