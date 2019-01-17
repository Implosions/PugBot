package pugbot.core.entities.menus;

import java.awt.Color;

import net.dv8tion.jda.core.entities.Member;
import pugbot.core.entities.PUGTeam;

public class PUGPickMenu extends TurnBasedEmbedMenu {

	private PUGTeam team = new PUGTeam();
	
	public PUGPickMenu(Member owner, PUGPickMenuController controller) {
		super(owner, controller);
		team.setCaptain(owner);
	}

	@Override
	public void fieldButtonClick(int fieldIndex) {
		if(!canPick()) {
			return;
		}
		
		setPickIndex(fieldIndex);
		getController().menuActionTaken(this);
	}

	@Override
	public void utilityButtonClick(String emoteName) {	
	}
	
	@Override
	protected void setEmbed() {
		super.setEmbed();
		
		StringBuilder sb = getEmbedBuilder().getDescriptionBuilder();
		
		sb.insert(0, "\n\n");
		sb.insert(0, getFormattedTeamsString());
	}
	
	public void setFinishedDescription() {
		getEmbedBuilder().setColor(Color.green)
						 .setDescription(getFormattedTeamsString());
	}
	
	public PUGTeam getPUGTeam() {
		return team;
	}
	
	private String getFormattedTeamsString() {
		return String.format("Teams:%n%s", ((PUGPickMenuController)getController()).getTeamsString());
	}
}
