package core.entities.menus;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class MapPickMenuManager extends MenuManager<MapPickMenuController, MapPickMenu> {
	
	private boolean picking = false;

	public MapPickMenuManager(Member owner, MapPickMenuController controller) {
		super(owner, controller);
	}

	@Override
	protected void createMenu() {
		MessageChannel channel = owner.getUser().openPrivateChannel().complete();
		
		menu = new MapPickMenu(channel, this, controller.getPickStyle());
	}

	@Override
	protected <T> void menuAction(int pageIndex, T action) {
		if(!picking) {
			return;
		}
		
		int fieldIndex = (int)action;
		int mapIndex = (pageIndex * getOptions().getPageMaxSize()) + fieldIndex;
		
		if(mapIndex >= controller.getMapPool().size()) {
			return;
		}
		
		controller.chooseMap(mapIndex);
		getOptions().removeOption(pageIndex, mapIndex);
		controller.managerActionTaken(this);
	}
	
	public void nextTurn() {
		picking = !picking;
	}
	
	public boolean canPick() {
		return picking;
	}
}
