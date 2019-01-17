package pugbot.core.entities.settings.queuesettings;

import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import pugbot.core.exceptions.InvalidUseException;

public class SettingVoiceChannelCategory extends QueueSetting<Category>{

	public SettingVoiceChannelCategory(Category value) {
		super(value);
	}

	@Override
	public String getName() {
		return "VoiceChannelCategory";
	}

	@Override
	public String getDescription() {
		return "The discord category that team voice channels will be created in";
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
		String categoryName = String.join(" ", args);
		
		List<Category> categories = manager.getServer().getGuild().getCategoriesByName(categoryName, true);
		
		if(categories.size() == 0){
			throw new InvalidUseException(String.format("Category '%s' does not exist", categoryName));
		}
		
		setValue(categories.get(0));
	}

	@Override
	public String getSaveString() {
		return getValue().getId();
	}
}
