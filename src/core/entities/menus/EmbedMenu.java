package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.Constants;
import core.entities.MenuRouter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public abstract class EmbedMenu implements IMenu {

	private Message message;
	protected MessageChannel channel;
	protected MenuManager<?, ?> manager;
	protected EmbedBuilder embedBuilder = new EmbedBuilder();
	protected List<String> fieldButtons;
	protected List<String> utilityButtons;
	protected int pageIndex = 0;

	public EmbedMenu(MessageChannel channel, MenuManager<?, ?> manager) {
		this.channel = channel;
		this.manager = manager;
	}

	protected void register() {
		update();
		MenuRouter.register(this);
	}

	private void unregister() {
		MenuRouter.unregister(getId());
	}

	@Override
	public void complete() {
		unregister();
	}

	@Override
	public void delete() {
		message.delete().queue();
		unregister();
	}

	@Override
	public long getId() {
		return message.getIdLong();
	}

	@Override
	public void buttonClick(String emoteName) {
		if (emoteName.equals(Constants.Emoji.FORWARD_BUTTON) && pageIndex < manager.getNumberOfPagesInMenu() - 1) {
			pageIndex++;
		} else if (emoteName.equals(Constants.Emoji.BACK_BUTTON) && pageIndex > 0) {
			pageIndex--;
		} else if (fieldButtons != null && fieldButtons.contains(emoteName)) {
			int index = fieldButtons.indexOf(emoteName);
			fieldButtonClick(index);
			return;
		} else if (utilityButtons != null && utilityButtons.contains(emoteName)) {
			utilityButtonClick(emoteName);
			return;
		} else {
			return;
		}

		update();
	}

	public abstract void fieldButtonClick(int index);

	public abstract void utilityButtonClick(String emoteName);

	protected void update() {
		Message updatedMessage;
		int menuSize = manager.getNumberOfPagesInMenu();

		embedBuilder.clearFields();

		if (menuSize > 0) {
			if (pageIndex == menuSize) {
				pageIndex--;
			}

			embedBuilder.setFooter(String.format("Page %d/%d", pageIndex + 1, menuSize), null);

			for (Field field : manager.getPage(pageIndex)) {
				embedBuilder.addField(field);
			}
		} else {
			embedBuilder.setFooter(null, null);
		}

		updatedMessage = new MessageBuilder().setEmbed(embedBuilder.build()).build();

		if (message != null) {
			message.editMessage(updatedMessage).queue();
		} else {
			message = channel.sendMessage(updatedMessage).complete();

			for (String emote : getButtonList()) {
				message.addReaction(emote).queue();
			}
		}
	}

	private List<String> getButtonList() {
		List<String> buttons = new ArrayList<String>();

		if (fieldButtons != null) {
			for (String button : fieldButtons) {
				buttons.add(button);
			}
		}

		if (utilityButtons != null) {
			for (String button : utilityButtons) {
				buttons.add(button);
			}
		}

		if (manager.getNumberOfPagesInMenu() > 0) {
			buttons.add(fieldButtons.size(), Constants.Emoji.FORWARD_BUTTON);
			buttons.add(0, Constants.Emoji.BACK_BUTTON);
		}

		return buttons;
	}
}
