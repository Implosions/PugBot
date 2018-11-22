package core.entities.menus;

import java.awt.Color;
import java.util.List;

import core.entities.menus.MapPickMenuController.PickStyle;
import net.dv8tion.jda.core.entities.MessageChannel;

public class MapPickMenu extends EmbedMenu {
	
	PickStyle pickStyle;
	
	public MapPickMenu(MessageChannel channel, MenuManager<?, ?> manager, PickStyle style) {
		super(channel, manager);
		pickStyle = style;
		String title = String.format("%s maps until the limit is reached", style.toString());
		
		embedBuilder.setTitle(title);
		register();
	}

	@Override
	public void fieldButtonClick(int index) {
		manager.menuAction(pageIndex, index);
	}

	@Override
	public void utilityButtonClick(String emoteName) {
	}
	
	public void update(boolean picking, int picksRemaining, String pickedMaps) {
		Color color;
		StringBuilder description = new StringBuilder();;
		
		if(pickedMaps.length() > 0) {
			description.append(pickedMaps + "\n\n");
		}
		
		if(pickStyle == PickStyle.PICK) {
			description.append("Picks ");
		} else {
			description.append("Bans ");
		}
		
		description.append("remaining: " + picksRemaining + "\n\n");
		
		if(picking) {
			color = Color.green;
			description.append("Your turn to choose");
		} else {
			color = Color.yellow;
			description.append("Your opponent is choosing");
		}
		
		embedBuilder.setColor(color)
					.setDescription(description.toString());
		update();
	}
	
	public void updateFinished(List<String> maps) {
		String mapsString = String.join(", ", maps);
		
		embedBuilder.setDescription("**Maps:** " + mapsString);
		embedBuilder.setColor(Color.green);
		update();
	}
}
