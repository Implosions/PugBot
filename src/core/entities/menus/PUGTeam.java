package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class PUGTeam extends MenuManager<PUGPickMenuController, PUGPickMenu> {

	private List<Member> players;
	private boolean picking;
	
	public PUGTeam(Member owner, PUGPickMenuController controller) {
		super(owner, controller);
		
		players = new ArrayList<Member>();
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
		Member pick = controller.getPlayerPool().get(index);
		
		if(!controller.getPickedPlayers().contains(pick)){
			players.add(pick);
			controller.managerActionTaken(this);
		}
	}

	public boolean isPicking(){
		return picking;
	}
	
	public void nextTurn(){
		picking = !picking;
	}
	
	public List<Member> getPlayers(){
		return players;
	}
	
	public int getTeamSize(){
		return players.size();
	}
	
	public Member getLastPick(){
		return players.get(players.size() - 1);
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(owner.getEffectiveName() + ": ");
		
		if(players.size() > 0){
			for(Member player : players){
				builder.append(player.getEffectiveName() + ", ");
			}
			
			builder.delete(builder.length() - 2, builder.length());
		}
		
		return builder.toString();
	}
	
	@Override
	public void complete(){
		menu.updatePickingFinished(controller.getTeamsString());
		super.complete();
	}
}
