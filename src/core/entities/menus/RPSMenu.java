package core.entities.menus;

import core.util.Trigger;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;

public class RPSMenu extends PrivateMenu{
	private User p1;
	private User p2;
	
	public RPSMenu(User p1, User p2, Trigger trigger){
		this.p1 = p1;
		this.p2 = p2;
		this.trigger = trigger;
		p1.getJDA().addEventListener(this);
	}
	
	public enum RPS{
		ROCK,
		PAPER,
		SCISSORS;
	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event){
		
	}
	
	protected void createMenuItems(){
		for(User u : new User[]{p1, p2}){
			MessageChannel c = u.openPrivateChannel().complete();
			menuItemList.add(new MenuItem(c, "Rock", "u2705"));
			menuItemList.add(new MenuItem(c, "Paper", "u2705"));
			menuItemList.add(new MenuItem(c, "Scissors", "u2705"));
		}
	}
	
	@Override
	public void complete() {
		for(MenuItem m : menuItemList){
			m.remove();
		}
		trigger.activate();
	}
	
	class Turn {
		private User player;
		private RPS rps;
		private boolean ready = false;
		
		private Turn(User player){
			this.player = player;
		}
		
		public void setRPS(RPS rps){
			this.rps = rps;
			this.ready = true;
		}
		
		public void clear(){
			rps = null;
			ready = false;
		}
		
		public boolean ready(){
			return ready;
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
