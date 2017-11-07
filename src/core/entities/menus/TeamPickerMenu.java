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
	private List<String> pickOrder;
	private Trigger trigger;
	private Integer turnCount = 1;
	private boolean finished = false;
	private boolean snake;
	
	public TeamPickerMenu(User[] captains, List<User> players, Trigger trigger, boolean snake){
		this.menus = new Menu[]{new Menu(captains[0].openPrivateChannel().complete()), new Menu(captains[1].openPrivateChannel().complete())};
		this.teams = new Team[]{new Team(captains[0]), new Team(captains[1])};
		this.playerPool = players;
		this.trigger = trigger;
		this.snake = snake;
		
		chooseOrder();
		captains[1].getJDA().addEventListener(this);
	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		if((event.getReaction().getEmote().getName().equals(CHECKMARK) || event.getReaction().getEmote().getName().equals(X)) 
				&& !event.getUser().isBot()){
			MenuItem mi = getMenu(event.getChannel()).getMenuItem(event.getMessageId());
			
			if(mi != null){
				String text = mi.getText();
				if(text.equals("Take first pick?")){
					if(event.getReaction().getEmote().getName().equals(X)){
						teams[1].picking = true;
					}else{
						teams[0].picking = true;
					}
					getMenu(event.getChannel()).deleteMenuItem(mi);
					createMenuItems();
				}else{
					User player = getPlayer(text);
					if(player != null){
						Team t = getTeam(event.getUser());
						if(t.picking){
							t.members.add(player);
							for(Menu m : menus){
								m.deleteMenuItem(m.getMenuItemByText(text));
							}
							nextTurn();
						}
					}
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
		teams[0].getCaptain().getJDA().removeEventListener(this);
		if(finished){
			trigger.activate();
			String s = String.format("Teams picked:%nRED - %s%nBLUE - %s", teams[0].toString(), teams[1].toString());
			for(Menu m : menus){
				m.editStatusMessage(s);
			}
			for(User p : playerPool){
				p.openPrivateChannel().complete().sendMessage(s).complete();
			}
		}else{
			for(Menu m : menus){
				m.deleteStatusMessage();
				m.deleteMenuItems();
			}
		}
	}
	
	private void pick(){
		for(Team t : teams){
			Menu m = getMenu(t.getCaptain().openPrivateChannel().complete());
			String s = String.format("Teams:%n%s%n%s%n", teams[0].toString(), teams[1].toString());
			if(t.picking){
				s += "Your turn to pick:";
			}else{
				s += "Waiting for your opponent to pick...";
			}
			m.editStatusMessage(s);
		}
	}
	
	private void nextTurn(){
		turnCount++;
		if (turnCount <= playerPool.size()) {
			if(!(playerPool.size() > 9 && snake && turnCount == playerPool.size() - 2)){
				for (Team t : teams) {
					t.picking = !t.picking;
				}
			}
			pick();
		}else{
			finished = true;
			complete();
		}
	}
	
	private User getPlayer(String name){
		for(User player : playerPool){
			if(player.getName().equals(name)){
				return player;
			}
		}
		return null;
	}
	
	private Menu getMenu(MessageChannel c){
		for(Menu m : menus){
			if(m.getChannel().equals(c)){
				return m;
			}
		}
		return null;
	}
	
	private Team getTeam(User captain){
		for(Team t : teams){
			if(t.getCaptain().equals(captain)){
				return t;
			}
		}
		return null;
	}
	
	public boolean finished(){
		return finished;
	}
	
	public String[] getPickOrder(){
		return (String[])pickOrder.toArray();
	}
	
	private class Team {
		private User captain;
		public List<User> members = new ArrayList<User>();
		public boolean picking = false;
		
		public Team(User captain){
			this.captain = captain;
		}
		
		public User getCaptain(){
			return captain;
		}
		
		public String toString(){
			String names = "";
			if(members.size() > 0){
				for(User m : members){
					names += m.getName() + ", ";
				}
				names = names.substring(0, names.length() - 2);
			}
			return String.format("%s: %s", captain.getName(), names);
		}
	}
}
