package core.entities.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import core.entities.PUGTeam;
import net.dv8tion.jda.core.entities.Member;

public class PUGPickMenuController extends MenuController<PUGPickMenu> {
	
	private List<Member> playerPool;
	private List<Member> pickedPlayers = new ArrayList<Member>();
	private int picksRemaining = 0;
	private ListIterator<Integer> pickIterator;
	
	public PUGPickMenuController(Member captain1, Member captain2, List<Member> playerPool, String pickPattern) {
		PUGPickMenu[] menus = new PUGPickMenu[2];
		menus[0] = new PUGPickMenu(captain1, this);
		menus[1] = new PUGPickMenu(captain2, this);
		setMenus(menus);
		
		this.playerPool = new ArrayList<Member>(playerPool);
		parsePickPattern(pickPattern);
		
		String title = "Captaining vs. ";
		getMenu(0).setTitle(title + captain2.getEffectiveName());
		getMenu(1).setTitle(title + captain1.getEffectiveName());
		
		getMenu(0).nextTurn();
		getMenu(1).setEmbed();
	}
	
	@Override
	protected synchronized void menuActionTaken(IMenu sender) {
		PUGPickMenu menu = (PUGPickMenu)sender;
		int index = menu.getPickIndex();
		Member player = playerPool.get(index);
		
		pickedPlayers.add(player);
		playerPool.remove(player);
		getMenuOptions().removeOption(index);
		menu.getPUGTeam().addPlayer(player);
		
		picksRemaining--;
		
		if(picksRemaining == 0){
			picksRemaining = getNextPickCount();
			
			switchTurns();
		}
		
		if(playerPool.size() == 1){
			autoassignLastPick();
		}
		
		updateMenus();
		notifyAll();
	}
	
	@Override
	protected boolean checkCondition() {
		return playerPool.size() > 0;
	}

	@Override
	protected void complete() {
		getMenu(0).complete();
		getMenu(1).complete();
	}
	
	@Override
	protected MenuOptions buildMenuOptions(){
		MenuOptions options = new MenuOptions(5);
		
		for(Member m : playerPool) {
			options.addOption(m.getEffectiveName());
		}
		
		return options;
	}
	
	@Override
	public void updateMenus() {
		if(playerPool.isEmpty()) {
			getMenu(0).setFinishedDescription();
			getMenu(1).setFinishedDescription();
		}
		
		super.updateMenus();
	}
	
	public List<Member> getPlayerPool(){
		return playerPool;
	}
	
	public List<Member> getPickedPlayers(){
		return pickedPlayers;
	}
	
	private void switchTurns(){
		getMenu(0).nextTurn();
		getMenu(1).nextTurn();
	}
	
	public String getTeamsString(){
		return getMenu(0).getPUGTeam().toString() + System.lineSeparator() + getMenu(1).getPUGTeam().toString();
	}
	
	private void parsePickPattern(String pattern){
		List<Integer> pickPattern = new ArrayList<Integer>();
		String[] tokens = pattern.split(" ");
		String pickLoop;
		
		if(tokens.length == 2){
			picksRemaining = Integer.parseInt(tokens[0]);
			pickLoop = tokens[1];
		}else{
			pickLoop = tokens[0];
		}
		
		for(char digit : pickLoop.toCharArray()){
			pickPattern.add(Integer.parseInt(String.valueOf(digit)));
		}
		
		pickIterator = pickPattern.listIterator();
		
		if(picksRemaining == 0){
			picksRemaining = pickIterator.next();
		}
	}
	
	private int getNextPickCount(){
		if(!pickIterator.hasNext()){
			while(pickIterator.hasPrevious()){
				pickIterator.previous();
			}
		}
		
		return pickIterator.next();
	}
	
	private void autoassignLastPick(){
		PUGPickMenu menu = getMenu(0).canPick() ? getMenu(0) : getMenu(1);
		Member lastPlayer = playerPool.get(0);
		
		menu.getPUGTeam().addPlayer(lastPlayer);
		pickedPlayers.add(lastPlayer);
		playerPool.clear();
		getMenuOptions().clearOptions();
	}
	
	public PUGTeam getTeam1(){
		return getMenu(0).getPUGTeam();
	}
	
	public PUGTeam getTeam2(){
		return getMenu(1).getPUGTeam();
	}
}
