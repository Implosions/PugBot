package core.entities.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import core.entities.PUGTeam;
import net.dv8tion.jda.core.entities.Member;

public class PUGPickMenuController extends MenuController<PUGMenuManager>{
	
	private List<Member> playerPool;
	private List<Member> pickedPlayers = new ArrayList<Member>();
	private int picksRemaining = 0;
	private ListIterator<Integer> pickIterator;
	
	public PUGPickMenuController(Member captain1, Member captain2, List<Member> playerPool, String pickPattern) {
		manager1 = new PUGMenuManager(captain1, this);
		manager2 = new PUGMenuManager(captain2, this);
		this.playerPool = new ArrayList<Member>(playerPool);
		parsePickPattern(pickPattern);
		
		manager1.nextTurn();
	}
	
	@Override
	protected synchronized void managerActionTaken(PUGMenuManager manager) {
		Member player = manager.getLastPick();
		
		pickedPlayers.add(player);
		playerPool.remove(player);
		
		picksRemaining--;
		
		if(picksRemaining == 0){
			picksRemaining = getNextPickCount();
			
			switchTurns();
		}
		
		updateMenus();
		notifyAll();
	}
	
	@Override
	protected void onMenuCreation() {
		updateMenus();
	}
	
	@Override
	protected boolean checkCondition() {
		return playerPool.size() > 1;
	}

	@Override
	protected void complete() {
		if(playerPool.size() == 1){
			autoassignLastPick();
		}
		
		manager1.complete();
		manager2.complete();
	}
	
	@Override
	protected void buildMenuOptions(){
		options = new MenuOptions(5);
		
		for(Member m : playerPool) {
			options.addOption(m.getEffectiveName());
		}
	}
	
	public List<Member> getPlayerPool(){
		return playerPool;
	}
	
	public List<Member> getPickedPlayers(){
		return pickedPlayers;
	}
	
	private void switchTurns(){
		manager1.nextTurn();
		manager2.nextTurn();
	}
	
	public String getTeamsString(){
		return manager1.getPUGTeam().toString() + System.lineSeparator() + manager2.getPUGTeam().toString();
	}
	
	private void updateMenus(){
		if(manager1.isPicking()){
			manager1.getMenu().updateYourTurn(getTeamsString());
			manager2.getMenu().updateOpponentsTurn(getTeamsString());
		}else{
			manager2.getMenu().updateYourTurn(getTeamsString());
			manager1.getMenu().updateOpponentsTurn(getTeamsString());
		}
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
		PUGMenuManager team = manager1.isPicking() ? manager1 : manager2;
		Member lastPlayer = playerPool.get(0);
		
		team.addPlayer(lastPlayer);
		pickedPlayers.add(lastPlayer);
		playerPool.clear();
		options.clearOptions();
	}
	
	public PUGTeam getTeam1(){
		return manager1.getPUGTeam();
	}
	
	public PUGTeam getTeam2(){
		return manager2.getPUGTeam();
	}
}
