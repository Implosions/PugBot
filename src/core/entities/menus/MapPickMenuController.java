package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;

public class MapPickMenuController extends MenuController<MapPickMenuManager> {
	
	private int mapCount;
	private List<String> mapPool = new ArrayList<>();
	private List<String> pickedMaps = new ArrayList<>();
	private PickStyle pickStyle;
	
	public enum PickStyle {
		PICK,
		BAN
	}
	
	public MapPickMenuController(Member p1, Member p2, int mapCount, List<String> maps, PickStyle style) {
		pickStyle = style;
		this.mapCount = mapCount;
		mapPool.addAll(maps);
		manager1 = new MapPickMenuManager(p1, this);
		manager2 = new MapPickMenuManager(p2, this);
		
		manager1.nextTurn();
	}

	@Override
	protected synchronized void managerActionTaken(MapPickMenuManager manager) {
		manager1.nextTurn();
		manager2.nextTurn();
		updateMenus();
		notifyAll();
	}

	@Override
	protected boolean checkCondition() {
		if(pickStyle == PickStyle.PICK) {
			return pickedMaps.size() != mapCount;
		}
		
		return mapPool.size() > mapCount;
	}

	@Override
	protected void complete() {
		if(pickStyle == PickStyle.BAN) {
			pickedMaps.addAll(mapPool);
			options.clearOptions();
		}
		
		manager1.getMenu().updateFinished(pickedMaps);
		manager2.getMenu().updateFinished(pickedMaps);
		manager1.complete();
		manager2.complete();
	}

	@Override
	protected void buildMenuOptions() {
		options = new MenuOptions(5);
		
		for(String map : mapPool) {
			options.addOption(map);
		}
	}

	@Override
	protected void onMenuCreation() {
		updateMenus();
	}
	
	public List<String> getMapPool(){
		return mapPool;
	}
	
	public List<String> getPickedMaps(){
		return pickedMaps;
	}

	public void chooseMap(int index) {
		String map = mapPool.get(index);
		
		mapPool.remove(index);
		
		if(pickStyle == PickStyle.PICK) {
			pickedMaps.add(map);
		}
	}
	
	private void updateMenus() {
		String pickedMapsString = String.join(", ", pickedMaps);
		
		manager1.getMenu().update(manager1.canPick(), getRemainingTurns(), pickedMapsString);
		manager2.getMenu().update(manager2.canPick(), getRemainingTurns(), pickedMapsString);
	}
	
	public PickStyle getPickStyle() {
		return pickStyle;
	}
	
	private int getRemainingTurns() {
		if(pickStyle == PickStyle.PICK) {
			return mapCount - pickedMaps.size();
		}
		
		return mapPool.size() - mapCount;
	}
}
