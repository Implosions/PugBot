package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class PUGPickMenuController extends MenuController<PUGTeam>{
	
	private List<Member> playerPool;
	private List<Member> pickedPlayers = new ArrayList<Member>();;
	
	public PUGPickMenuController(Member captain1, Member captain2, List<Member> playerPool) {
		pageSize = 5;
		manager1 = new PUGTeam(captain1, this);
		manager2 = new PUGTeam(captain2, this);
		this.playerPool = new ArrayList<Member>(playerPool);
		generatePages();
		
		manager1.nextTurn();		
		updateMenus();
	}
	
	@Override
	protected synchronized void managerActionTaken(PUGTeam manager) {
		Member player = manager.getLastPick();
		int index = playerPool.indexOf(player);
		
		pickedPlayers.add(player);
		playerPool.remove(player);
		removeField(index);
		generatePages();
		
		switchTurns();
		updateMenus();
		notifyAll();
	}
	
	@Override
	protected boolean checkCondition() {
		return playerPool.size() != 0;
	}

	@Override
	protected void complete() {
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
}
