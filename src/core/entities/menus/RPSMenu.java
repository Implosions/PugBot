package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.util.Trigger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;

public class RPSMenu extends PrivateMenu{
	private Turn turns[] = new Turn[2];
	private List<Message> statusMessageList = new ArrayList<Message>();
	
	public RPSMenu(User p1, User p2, Trigger trigger){
		this.turns[0] = new Turn(p1);
		this.turns[1] = new Turn(p2);
		this.trigger = trigger;
		createMenuItems();
		p1.getJDA().addEventListener(this);
	}
	
	public enum RPS{
		ROCK,
		PAPER,
		SCISSORS;
	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		if(event.getReaction().getEmote().getName().equals("\u2705") && !event.getUser().isBot()){
			MenuItem mi = getMenuItem(event.getMessageId());
			if(mi != null){
				RPS rps = null;
				switch (mi.getText()){
				case "Rock": rps = RPS.ROCK; break;
				case "Paper": rps = RPS.PAPER; break;
				case "Scissors": rps = RPS.SCISSORS; break;
				}
				Integer i = 0;
				if(event.getUser() != turns[i].getPlayer()){
					i++;
				}
				turns[i].setRPS(rps);
				statusMessageList.get(i).editMessage("Waiting for opponent").complete();
				check();
			}
		}
	}
	
	protected void createMenuItems(){
		for(User u : new User[]{turns[0].getPlayer(), turns[1].getPlayer()}){
			MessageChannel c = u.openPrivateChannel().complete();
			statusMessageList.add(c.sendMessage("Make your selection:").complete());
			menuItemList.add(new MenuItem(c, "Rock", "\u2705"));
			menuItemList.add(new MenuItem(c, "Paper", "\u2705"));
			menuItemList.add(new MenuItem(c, "Scissors", "\u2705"));
		}
	}
	
	@Override
	public void complete() {
		for(MenuItem m : menuItemList){
			m.remove();
		}
		turns[0].getPlayer().getJDA().removeEventListener(this);
		trigger.activate();
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
		for(Message m : statusMessageList){
			m.editMessage("You have tied with your opponent").complete();
		}
	}
	
	private void gameWin(User u){
		for(Message m : statusMessageList){
			if(m.getChannel().equals(u.openPrivateChannel().complete())){
				m.editMessage("You have won!").complete();
			}else{
				m.editMessage("You have lost!").complete();
			}
		}
		complete();
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
