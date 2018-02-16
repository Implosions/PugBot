package core.entities.menus;

import core.entities.menus.Menu.MenuStatus;
import core.util.Trigger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class RPSMenu extends ListenerAdapter{
	
	private final String CHECKMARK = "\u2705";
	private Trigger trigger;
	private Turn turns[];
	private Menu menus[];
	private boolean finished = false;
	private User[] result = new User[2];
	
	public RPSMenu(User p1, User p2, Trigger trigger){
		String message = "Rock, Paper, Scissors!\nYou are facing %s";
		try{
			p1.openPrivateChannel().complete().sendMessage(String.format(message, p2.getName())).complete();
			p2.openPrivateChannel().complete().sendMessage(String.format(message, p1.getName())).complete();
		}catch(Exception ex){
			System.out.println("Error sending private message.\n" + ex.getMessage());
		}
		
		this.trigger = trigger;
		this.turns = new Turn[]{new Turn(p1), new Turn(p2)};
		this.menus = new Menu[]{new Menu(p1.openPrivateChannel().complete()), new Menu(p2.openPrivateChannel().complete())};
		createMenuItems();
		p1.getJDA().addEventListener(this);
		System.out.println("RPS menu created");
	}
	
	public enum RPS{
		ROCK,
		PAPER,
		SCISSORS;
	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		if(event.getReaction().getEmote().getName().equals(CHECKMARK) && !event.getUser().isBot()){

			MenuItem mi = getMenu(event.getChannel()).getMenuItem(event.getMessageId());
			
			if(mi != null){
				RPS rps = null;
				switch (mi.getText()){
				case "Rock": rps = RPS.ROCK; break;
				case "Paper": rps = RPS.PAPER; break;
				case "Scissors": rps = RPS.SCISSORS; break;
				}
				getTurn(event.getUser()).setRPS(rps);
				getMenu(event.getChannel()).editStatusMessage("Waiting for opponent...");
				check();
			}
		}
	}
	
	private void createMenuItems(){
		for(Menu m : menus){
			try{
				m.createStatusMessage("Make your selection:");
				m.createMenuItem("Rock", CHECKMARK);
				m.createMenuItem("Paper", CHECKMARK);
				m.createMenuItem("Scissors", CHECKMARK);
				m.changeMenuStatus(MenuStatus.COMPLETE);
			}catch(Exception ex){
				System.out.println("Error creating menuitems.\n" + ex.getMessage());
			}
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
	
	private Turn getTurn(User u){
		for(Turn t : turns){
			if(t.getPlayer().equals(u)){
				return t;
			}
		}
		return null;
	}
	
	public boolean finished(){
		return finished;
	}
	
	public void complete() {
		for(Menu m : menus){
			m.changeMenuStatus(MenuStatus.ABORTED);
			m.deleteMenuItems();
		}
		turns[0].getPlayer().getJDA().removeEventListener(this);
		
		if(finished){
			trigger.activate();
		}else{
			for(Menu m : menus){
				m.deleteStatusMessage();
			}
		}
	}
	
	private void check(){
		if(turns[0].ready() && turns[1].ready()){
			Integer result = turns[0].compare(turns[1]);
			
			turns[0].newUpdateMessage(turns[0].rps, turns[1].rps);
			turns[1].newUpdateMessage(turns[1].rps, turns[0].rps);
			
			switch(result){
			case 0: gameTie(); break;
			case 1: gameWin(turns[0].getPlayer()); break;
			case -1: gameWin(turns[1].getPlayer()); break;
			}
		}
	}
	
	private void gameTie(){
		turns[0].clear();
		turns[1].clear();
		for(Menu m : menus){
			m.editStatusMessage("You have tied with your opponent, choose again");
		}
	}
	
	private void gameWin(User u){
		finished = true;
		
		
		result[1] = (result[0] != turns[0].getPlayer()) ? turns[0].getPlayer() : turns[1].getPlayer();
		
		for(Menu m : menus){
			if(m.getChannel().equals(u.openPrivateChannel().complete())){
				m.editStatusMessage("You have won!");
			}else{
				m.editStatusMessage("You have lost!");
			}
		}
		complete();
	}
	
	public User[] getResult(){
		return this.result;
	}
	
	private class Turn {
		private User player;
		private RPS rps = null;
		private Message lastUpdateMessage = null;
		
		public Turn(User player){
			this.player = player;
		}
		
		public void setRPS(RPS rps){
			this.rps = rps;
		}
		
		public void clear(){
			rps = null;
		}
		
		public boolean ready(){
			return rps != null;
		}
		
		public User getPlayer(){
			return player;
		}
		
		public Integer compare(Turn opponent){
			if(rps == opponent.rps){
				return 0;
			}
			switch(rps){
			case ROCK: return opponent.rps == RPS.SCISSORS ? 1 : -1;
			case PAPER: return opponent.rps == RPS.ROCK ? 1 : -1;
			case SCISSORS: return opponent.rps == RPS.PAPER ? 1 : -1;
			}
			return null;
		}
		
		public void newUpdateMessage(RPS p1Turn, RPS p2Turn){
			if(lastUpdateMessage != null){
				lastUpdateMessage.delete().complete();
			}
			try{
				lastUpdateMessage = player.openPrivateChannel().complete()
						.sendMessage(String.format
								("You chose %s%nYour opponent chose %s", p1Turn.toString(), p2Turn.toString())).complete();
			}catch(Exception ex){
				System.out.println("Error sending private message.\n" + ex.getMessage());
			}
		}
	}
}
