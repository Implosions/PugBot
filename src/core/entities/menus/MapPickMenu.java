package core.entities.menus;

import java.awt.Color;
import java.util.List;

import core.entities.menus.MapPickMenuController.PickStyle;
import net.dv8tion.jda.core.entities.Member;

public class MapPickMenu extends TurnBasedEmbedMenu {
	
	PickStyle pickStyle;
	
	public MapPickMenu(Member owner, MapPickMenuController controller, PickStyle style) {
		super(owner, controller);
		pickStyle = style;
		String title = String.format("%s maps until the limit is reached", style.toString());
		
		getEmbedBuilder().setTitle(title);
	}

	@Override
	public void fieldButtonClick(int index) {
		if(!canPick()) {
			return;
		}
		
		setPickIndex(index);
		getController().menuActionTaken(this);
	}

	@Override
	public void utilityButtonClick(String emoteName) {
	}
	
	@Override
	protected void setEmbed() {
		super.setEmbed();
		
		StringBuilder sb = getEmbedBuilder().getDescriptionBuilder();
		String desc = ((MapPickMenuController)getController()).getMenuDescription();
		
		sb.insert(0, "\n\n");
		sb.insert(0, desc);
	}
	
//	public void update(int picksRemaining, String pickedMaps) {
//		Color color;
//		
//		
//		if(canPick()) {
//			color = Color.green;
//			description.append("Your turn to choose");
//		} else {
//			color = Color.yellow;
//			description.append("Your opponent is choosing");
//		}
//		
//		getEmbedBuilder().setColor(color)
//					.setDescription(description.toString());
//		update();
//	}
	
	public void updateFinished(List<String> maps) {
		String mapsString = String.join(", ", maps);
		
		getEmbedBuilder().setDescription("**Maps:** " + mapsString);
		getEmbedBuilder().setColor(Color.green);
		update();
	}
}
