package core.entities.menus;

import java.awt.Color;

import net.dv8tion.jda.core.entities.Member;

public abstract class TurnBasedEmbedMenu extends EmbedMenu {

	private Member owner;
	private boolean turn = false;
	private int pickIndex;
	
	public TurnBasedEmbedMenu(Member owner, MenuController<?> controller) {
		super(owner.getUser().openPrivateChannel().complete(), controller);
		this.owner = owner;
	}

	public void nextTurn() {
		turn = !turn;
		setEmbed();
	}
	
	public boolean canPick() {
		return turn;
	}
	
	public Member getOwner() {
		return owner;
	}
	
	public void setPickIndex(int index) {
		pickIndex = index;
	}
	
	public int getPickIndex() {
		return pickIndex;
	}
	
	protected void setEmbed() {
		if(canPick()) {
			getEmbedBuilder().setColor(Color.GREEN)
			 				 .setDescription("Your turn to pick!");
		} else {
			getEmbedBuilder().setColor(Color.YELLOW)
			 				 .setDescription("Waiting for opponent to pick...");
		}
	}
}
