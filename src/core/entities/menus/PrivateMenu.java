package core.entities.menus;

import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;

public abstract class PrivateMenu extends Menu{
	public abstract void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event);
}
