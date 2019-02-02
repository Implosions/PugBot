package pugbot.core.entities.menus;

import java.awt.Color;

import net.dv8tion.jda.core.entities.Member;
import pugbot.core.Constants;

public class ConfirmationMenu extends EmbedMenu implements IMenuController {
	
	private boolean result = false;
	private boolean cancelled = false;
	
	public ConfirmationMenu(Member member, String title) {
		super(member.getUser().openPrivateChannel().complete());
		
		getEmbedBuilder().setTitle(title)
						 .setColor(Color.yellow)
						 .setDescription(String.format("%sAccept or %sDecline", 
											Constants.Emoji.CHECKMARK,
											Constants.Emoji.X));
		
		MenuOptions options = new MenuOptions(0);
		options.addUtilityButton(Constants.Emoji.CHECKMARK);
		options.addUtilityButton(Constants.Emoji.X);
		setMenuOptions(options);
	}
	
	@Override
	public synchronized void start() {
		super.start();
		
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

	public synchronized void cancel() {
		cancelled = true;
		
		notifyAll();
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
}
