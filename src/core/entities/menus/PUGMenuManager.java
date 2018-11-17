package core.entities.menus;

import java.util.List;

import core.entities.PUGTeam;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class PUGMenuManager extends MenuManager<PUGPickMenuController, PUGPickMenu> {

	private PUGTeam team;
	private boolean picking;
	
	public PUGMenuManager(Member owner, PUGPickMenuController controller) {
		super(owner, controller);
		
		team = new PUGTeam(owner);
	}

	@Override
	protected void createMenu() {
		MessageChannel channel = owner.getUser().openPrivateChannel().complete();
		
		menu = new PUGPickMenu(channel, this, controller.getPlayerPool());
	}

	@Override
	protected <T> void menuAction(T action) {
		if(!picking){
			return;
		}
		
		int index = (int)action;
		List<Member> playerPool = controller.getPlayerPool();
		
		if(index >= playerPool.size()){
			return;
		}
		
		Member pick = playerPool.get(index);
		
		if(!controller.getPickedPlayers().contains(pick)){
			team.addPlayer(pick);
			controller.managerActionTaken(this);
		}
	}

	public boolean isPicking(){
		return picking;
	}
	
	public void nextTurn(){
		picking = !picking;
	}
	
	public PUGTeam getPUGTeam(){
		return team;
	}
	
	public int getTeamSize(){
		return team.getPlayers().size();
	}
	
	public Member getLastPick(){
		List<Member> players = team.getPlayers();
		
		return players.get(players.size() - 1);
	}
	
	protected void addPlayer(Member player){
		team.addPlayer(player);
	}
	
	@Override
	public void complete(){
		menu.updatePickingFinished(controller.getTeamsString());
		super.complete();
	}
}
