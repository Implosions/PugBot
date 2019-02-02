package pugbot.core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import pugbot.core.Constants;
import pugbot.core.entities.MenuRouter;

public abstract class EmbedMenu implements IMenu {

	private Message message;
	private MessageChannel channel;
	private EmbedBuilder embedBuilder = new EmbedBuilder();
	private int pageIndex = 0;
	private MenuController<?> controller;
	private MenuOptions options;
	
	public EmbedMenu(MessageChannel channel) {
		this.channel = channel;
	}
	
	public EmbedMenu(MessageChannel channel, MenuController<?> controller) {
		this.channel = channel;
		this.controller = controller;
	}
	
	public void start() {
		if(controller != null) {
			setMenuOptions(controller.getMenuOptions());
		}
		
		update();
		register();
	}

	private void register() {
		MenuRouter.register(this);
	}

	private void unregister() {
		if(message != null) {
			MenuRouter.unregister(getId());
		}
	}

	@Override
	public void complete() {
		unregister();
	}

	@Override
	public void delete() {
		if(message != null) {
			message.delete().queue();
		}
		
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
			update();
		} else if (emoteName.equals(Constants.Emoji.BACK_BUTTON) && pageIndex > 0) {
			pageIndex--;
			update();
		} else if (fieldButtons.contains(emoteName)) {
			int pageSize = options.getPageMaxSize();
			int index = fieldButtons.indexOf(emoteName) + (pageIndex * pageSize);
			
			if(index < options.size()) {
				fieldButtonClick(index);
			}
			
		} else if (utilityButtons.contains(emoteName)) {
			utilityButtonClick(emoteName);
		}
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

		try {
			if (message != null) {
				message.editMessage(updatedMessage).queue();
			} else {
				message = channel.sendMessage(updatedMessage).complete();
		
				for (String emote : getButtonList()) {
					message.addReaction(emote).queue();
				}
			}
		} catch(ErrorResponseException ex) {
			controller.cancel();
			
			PrivateChannel pChannel = (PrivateChannel)channel;
			String err = String.format("User '%s' has blocked me", pChannel.getUser().getName());
			
			throw new IllegalStateException(err);
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
	
	public int getPageIndex() {
		return pageIndex;
	}
	
	public void setTitle(String title) {
		embedBuilder.setTitle(title);
	}
	
	public MenuOptions getMenuOptions() {
		return options;
	}
	
	public void setMenuOptions(MenuOptions options) {
		this.options = options;
	}
	
	public MenuController<?> getController() {
		return controller;
	}
	
	public EmbedBuilder getEmbedBuilder() {
		return embedBuilder;
	}
}
