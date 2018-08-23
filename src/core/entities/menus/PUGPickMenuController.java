package core.entities.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class PUGPickMenuController extends MenuController<PUGTeam>{
	
	private List<Member> playerPool;
	private List<Member> pickedPlayers = new ArrayList<Member>();
	private int picksRemaining = 0;
	private ListIterator<Integer> pickIterator;
	
	public PUGPickMenuController(Member captain1, Member captain2, List<Member> playerPool, String pickPattern) {
		pageSize = 5;
		manager1 = new PUGTeam(captain1, this);
		manager2 = new PUGTeam(captain2, this);
		this.playerPool = new ArrayList<Member>(playerPool);
		generatePages();
		parsePickPattern(pickPattern);
		
		manager1.nextTurn();
	}
	
	@Override
	protected synchronized void managerActionTaken(PUGTeam manager) {
		Member player = manager.getLastPick();
		int index = playerPool.indexOf(player);
		
		pickedPlayers.add(player);
		playerPool.remove(player);
		removeField(index);
		generatePages();
		
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
	protected void generatePages(){
		pages = new ArrayList<List<Field>>();
		List<Field> fields;
		
		for(int i = 0;i < (playerPool.size() / pageSize) + 1;i++){
			fields = new ArrayList<Field>();
			
			for(int j = 0;j < pageSize;j++){		
				int index = j + (i * pageSize);
				
				if(index == playerPool.size()){
					break;
				}
				
				String playerName = playerPool.get(index).getEffectiveName();
				
				fields.add(new Field(String.format("%d) %s", j + 1, playerName), "\u200b", false));
			}
			
			if(fields.size() > 0){
				pages.add(fields);
			}
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
		return manager1.toString() + System.lineSeparator() + manager2.toString();
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
		PUGTeam team = manager1.isPicking() ? manager1 : manager2;
		Member lastPlayer = playerPool.get(0);
		
		team.addPlayer(lastPlayer);
		pickedPlayers.add(lastPlayer);
		playerPool.clear();
		generatePages();
	}
	
	public int getTeam(Member player){
		return manager1.getPlayers().contains(player) ? 1 : 2;
	}
}
