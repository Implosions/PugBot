package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.util.Trigger;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TeamPickerMenu extends ListenerAdapter{
	
	private final String CHECKMARK = "\u2705";
	private final String X = "\u274C";
	private Menu[] menus;
	private Team[] teams;
	private List<User> playerPool;
	private Trigger trigger;
	
	public TeamPickerMenu(User[] captains, List<User> players, Trigger trigger){
		this.menus = new Menu[]{new Menu(captains[0].openPrivateChannel().complete()), new Menu(captains[1].openPrivateChannel().complete())};
		this.teams = new Team[]{new Team(captains[0]), new Team(captains[0])};
		this.playerPool = players;
		this.trigger = trigger;
		
		chooseOrder();
		captains[1].getJDA().addEventListener(this);
	}
	
	private enum Status{
		PICKING,
		WAITING;
	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		if((event.getReaction().getEmote().getName().equals(CHECKMARK) || event.getReaction().getEmote().equals(X)) && !event.getUser().isBot()){
			MenuItem mi = getMenu(event.getChannel()).getMenuItem(event.getMessageId());
			
			if(mi != null){
				if(mi.getText().equals("Take first pick?")){
					if(event.getReaction().getEmote().equals(X)){
						teams = new Team[]{teams[1], teams[0]};
					}
					mi.remove();
					teams[0].status = Status.PICKING;
					teams[1].status = Status.WAITING;
					createMenuItems();
				}
			}
		}
	}
	
	private void createMenuItems(){
		for(Menu m : menus){
			m.createStatusMessage("Initializing...");
			for(User player : playerPool){
				m.createMenuItem(player.getName(), CHECKMARK);
			}
		}
		pick();
	}
	
	private void chooseOrder(){
		menus[0].createMenuItem("Take first pick?", new String[]{CHECKMARK, X});
	}
	
	public void complete(){
		
	}
	
	private void pick(){
		for(Team t : teams){
			Menu m = getMenu(t.getCaptain().openPrivateChannel().complete());
			String s = String.format("%s%n%s%n", teams[0].toString(), teams[1].toString());
			if(t.status == Status.PICKING){
				s += "Your turn to pick:";
			}else{
				s += "Waiting for your opponent to pick...";
			}
			m.editStatusMessage(s);
		}
	}
	
	private Menu getMenu(MessageChannel c){
		for(Menu m : menus){
			if(m.getChannel().equals(c)){
				return m;
			}
		}
		return null;
	}
	
	private class Team {
		private User captain;
		public List<User> members = new ArrayList<User>();
		public Status status = null;
		
		public Team(User captain){
			this.captain = captain;
		}
		
		public User getCaptain(){
			return captain;
		}
		
		public String toString(){
			String names = null;
			for(User m : members){
				names += m.getName() + ", ";
			}
			names = names.substring(0, names.lastIndexOf(","));
			return String.format("%s: %s", captain.getName(), names);
		}
	}
}
