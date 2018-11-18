package core.entities.menus;

import java.awt.Color;

import core.Constants;
import net.dv8tion.jda.core.entities.Member;

public class RepickConfirmationMenu extends EmbedMenu {
	
	private boolean result = false;
	
	public RepickConfirmationMenu(Member user) {
		super(user.getUser().openPrivateChannel().complete());
		
		embedBuilder.setTitle("Your opponent has requested to repick the teams")
					.setColor(Color.yellow)
					.setDescription(String.format("%sAccept or %sDecline", 
											Constants.Emoji.CHECKMARK,
											Constants.Emoji.X));
		
		options = new MenuOptions(0);
		options.addUtilityButton(Constants.Emoji.CHECKMARK);
		options.addUtilityButton(Constants.Emoji.X);
		
		register();
		start();
	}
	
	private synchronized void start(){
		try {
			wait(5 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		delete();
	}

	@Override
	public void fieldButtonClick(int index) {}

	@Override
	public synchronized void utilityButtonClick(String emoteName) {
		if(emoteName.equals(Constants.Emoji.CHECKMARK)){
			result = true;
		}
		notifyAll();
	}
	
	public boolean getResult(){
		return result;
	}

}
