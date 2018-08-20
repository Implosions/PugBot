package core.entities.menus;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import core.Constants;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public class PUGPickMenu extends EmbedMenu {

	public PUGPickMenu(MessageChannel channel, PUGTeam manager, List<Member> playerPool) {
		super(channel, manager);
		
		fieldButtons = Arrays.asList(Constants.Emoji.NUMBER_1,
									 Constants.Emoji.NUMBER_2,
									 Constants.Emoji.NUMBER_3,
									 Constants.Emoji.NUMBER_4,
									 Constants.Emoji.NUMBER_5);
		embedBuilder.setTitle(String.format("Captaining vs. %s", manager.getOpponentOwner().getEffectiveName()));
		
		register();
	}

	@Override
	public void fieldButtonClick(int index) {
		manager.menuAction((pageIndex * fieldButtons.size()) + index);
	}

	@Override
	public void utilityButtonClick(String emoteName) {	
	}
	
	public void updateYourTurn(String teams) {
		embedBuilder.setColor(Color.green)
					.setDescription(String.format("Teams:%n%s%n%nYour turn to pick", teams));
		update();
	}
	
	public void updateOpponentsTurn(String teams) {
		embedBuilder.setColor(Color.yellow)
					.setDescription(String.format("Teams:%n%s%n%nYour opponent is picking", teams));
		update();
	}
	
	public void updatePickingFinished(String teams) {
		embedBuilder.setColor(Color.green)
					.setDescription(String.format("Teams:%n%s", teams));
		update();
	}
}
