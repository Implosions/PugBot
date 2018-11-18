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
	protected int pageIndex = 0;
	protected MenuOptions options;
	
	public EmbedMenu(MessageChannel channel) {
		this.channel = channel;
	}

	public EmbedMenu(MessageChannel channel, MenuManager<?, ?> manager) {
		this.channel = channel;
		this.manager = manager;
		this.options = manager.getOptions();
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
		int pages = options.getPageCount();
		List<String> fieldButtons = options.getFieldButtons();
		List<String> utilityButtons = options.getUtilityButtons();
		
		if (emoteName.equals(Constants.Emoji.FORWARD_BUTTON) && pageIndex < pages - 1) {
			pageIndex++;
		} else if (emoteName.equals(Constants.Emoji.BACK_BUTTON) && pageIndex > 0) {
			pageIndex--;
		} else if (fieldButtons != null && fieldButtons.contains(emoteName)) {
			int index = fieldButtons.indexOf(emoteName);
			fieldButtonClick(index);
			return;
		} else if (utilityButtons.contains(emoteName)) {
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

		embedBuilder.clearFields();

		int pageCount = options.getPageCount();
		
		if (pageCount > 0) {
			if (pageIndex == pageCount) {
				pageIndex--;
			}

			embedBuilder.setFooter(String.format("Page %d/%d", pageIndex + 1, pageCount), null);

			for (Field field : options.getPage(pageIndex)) {
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

		for (String button : options.getFieldButtons()) {
			buttons.add(button);
		}

		for (String button : options.getUtilityButtons()) {
			buttons.add(button);
		}

		if (options.getPageCount() > 1) {
			buttons.add(buttons.size(), Constants.Emoji.FORWARD_BUTTON);
			buttons.add(0, Constants.Emoji.BACK_BUTTON);
		}

		return buttons;
	}
}
