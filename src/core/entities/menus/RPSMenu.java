package core.entities.menus;

import core.util.Trigger;
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
		this.trigger = trigger;
		this.turns = new Turn[]{new Turn(p1), new Turn(p2)};
		this.menus = new Menu[]{new Menu(p1.openPrivateChannel().complete()), new Menu(p2.openPrivateChannel().complete())};
		createMenuItems();
		p1.getJDA().addEventListener(this);
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
			m.createStatusMessage("Make your selection:");
			m.createMenuItem("Rock", CHECKMARK);
			m.createMenuItem("Paper", CHECKMARK);
			m.createMenuItem("Scissors", CHECKMARK);
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
		
		// Super spaghetti, need to fix
		result[0] = u;
		if(u != turns[0].getPlayer()){
			result[1] = turns[0].getPlayer();
		}else{
			result[1] = turns[1].getPlayer();
		}
		
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
	}
}
