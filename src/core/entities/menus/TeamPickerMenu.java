package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.util.Trigger;
import net.dv8tion.jda.core.entities.Message;
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
	private List<String> pickOrder = new ArrayList<String>();
	private Trigger trigger;
	private Integer turnCount = 1;
	private boolean finished = false;
	private boolean snake;
	private String pickedTeamsString = null;
	
	public TeamPickerMenu(User[] captains, List<User> players, Trigger trigger, boolean snake){
		this.menus = new Menu[]{new Menu(captains[0].openPrivateChannel().complete()), new Menu(captains[1].openPrivateChannel().complete())};
		this.teams = new Team[]{new Team(captains[0]), new Team(captains[1])};
		this.playerPool = players;
		this.trigger = trigger;
		this.snake = snake;
		
		chooseOrder();
		captains[1].getJDA().addEventListener(this);
		System.out.println("Pick menu created");
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
							pickOrder.add(player.getId());
							for(Menu m : menus){
								m.deleteMenuItem(m.getMenuItemByText(text));
							}
							// more spaghetti
							for(Team team : teams){
								if(!team.picking){
									team.newUpdateMessage(text);
								}
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
			pickedTeamsString = String.format("Teams picked:%nRED - %s%nBLUE - %s", teams[0].toString(), teams[1].toString());
			trigger.activate();
			
			for(Menu m : menus){
				m.editStatusMessage(pickedTeamsString);
			}

			for(User p : playerPool){
				p.openPrivateChannel().complete().sendMessage(pickedTeamsString).complete();
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
			if(!(playerPool.size() > 7 && snake && turnCount == playerPool.size() - 1)){
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
		String[] s = new String[pickOrder.size()];
		pickOrder.toArray(s);
		return s;
	}
	
	public void sub(User target, User substitute){
		playerPool.remove(target);
		playerPool.add(substitute);
		
		for(Menu m : menus){
			MenuItem mi = m.getMenuItemByText(target.getName());
			if(mi != null){
				mi.setText(substitute.getName());
			}
		}
	}
	
	public String getPickedTeamsString(){
		return pickedTeamsString;
	}
	
	private class Team {
		private User captain;
		public List<User> members = new ArrayList<User>();
		public boolean picking = false;
		private Message lastUpdateMessage = null;
		
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
		
		public void newUpdateMessage(String playerName){
			if(lastUpdateMessage != null){
				lastUpdateMessage.delete().complete();
			}
			lastUpdateMessage = captain.openPrivateChannel().complete().sendMessage(String.format("Your opponent picked: %s", playerName)).complete();
		}
	}
}
