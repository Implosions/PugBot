package core.entities.menus;

import java.util.List;

import core.entities.PUGTeam;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class PUGMenuManager extends MenuManager<PUGPickMenuController, PUGPickMenu> {

	private PUGTeam team = new PUGTeam();
	private boolean picking;
	
	public PUGMenuManager(Member owner, PUGPickMenuController controller) {
		super(owner, controller);
		
		team.setCaptain(owner);
	}

	@Override
	protected void createMenu() {
		MessageChannel channel = owner.getUser().openPrivateChannel().complete();
		
		menu = new PUGPickMenu(channel, this);
	}

	@Override
	protected <T> void menuAction(int pageIndex, T action) {
		if(!picking){
			return;
		}
		
		int fieldIndex = (int)action;
		int playerIndex = (pageIndex * getOptions().getPageMaxSize()) + fieldIndex;
		
		List<Member> playerPool = controller.getPlayerPool();
		
		if(playerIndex >= playerPool.size()){
			return;
		}
		
		Member pick = playerPool.get(playerIndex);
		
		if(!controller.getPickedPlayers().contains(pick)){
			getOptions().removeOption(pageIndex, fieldIndex);
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
