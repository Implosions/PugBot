package pugbot.core.entities.settings.serversettings;

import java.util.List;

import net.dv8tion.jda.core.entities.TextChannel;
import pugbot.core.exceptions.InvalidUseException;

public class SettingPUGChannel extends ServerSetting<TextChannel> {

	public SettingPUGChannel(TextChannel value) {
		super(value);
	}

	@Override
	public String getName() {
		return "PUGChannel";
	}

	@Override
	public String getDescription() {
		return "The text-channel to focus PUG activities in";
	}

	@Override
	public String getValueString() {
		if(getValue() == null){
			return "N/A";
		}
		
		return getValue().getName();
	}

	@Override
	public void set(String[] args) {
		String channelName = String.join(" ", args);

		List<TextChannel> channels = manager.getServer().getGuild().getTextChannelsByName(channelName, true);
		
		if(channels.size() == 0){
			throw new InvalidUseException(String.format("Channel '%s' does not exist", channelName));
		}
		
		setValue(channels.get(0));
	}

	@Override
	public String getSaveString() {
		return getValue().getId();
	}
}
