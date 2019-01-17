package pugbot.core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;

public class MapPickMenuController extends MenuController<MapPickMenu> {
	
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
		
		MapPickMenu[] menus = new MapPickMenu[2];
		menus[0] = new MapPickMenu(p1, this, style);
		menus[1] = new MapPickMenu(p2, this, style);
		setMenus(menus);
		
		getMenu(0).nextTurn();
		getMenu(1).setEmbed();
	}

	@Override
	protected synchronized void menuActionTaken(IMenu sender) {
		int index = ((MapPickMenu)sender).getPickIndex();
		String map = mapPool.get(index);
		
		mapPool.remove(index);
		
		if(pickStyle == PickStyle.PICK) {
			pickedMaps.add(map);
		}
		
		getMenuOptions().removeOption(index);
		
		getMenu(0).nextTurn();
		getMenu(1).nextTurn();
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
		}
		
		getMenuOptions().clearOptions();
		getMenu(0).updateFinished(pickedMaps);
		getMenu(1).updateFinished(pickedMaps);
		getMenu(0).complete();
		getMenu(1).complete();
	}

	@Override
	protected MenuOptions buildMenuOptions() {
		MenuOptions options = new MenuOptions(5);
		
		for(String map : mapPool) {
			options.addOption(map);
		}
		
		return options;
	}
	
	public List<String> getMapPool(){
		return mapPool;
	}
	
	public List<String> getPickedMaps(){
		return pickedMaps;
	}
	
	public String getMenuDescription() {
		StringBuilder description = new StringBuilder();;
		
		if(pickedMaps.size() > 0) {
			description.append(String.join(", ", pickedMaps));
			description.append('\n');
			description.append('\n');
		}
		
		description.append("Total ");
		
		if(pickStyle == PickStyle.PICK) {
			description.append("picks ");
		} else {
			description.append("bans ");
		}
		
		description.append("remaining: ");
		description.append(getRemainingTurns());
		
		return description.toString();
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
