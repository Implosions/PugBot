package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import core.Constants;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class MenuOptions {
	private static final String[] FIELD_BUTTONS = {
			Constants.Emoji.NUMBER_1,
			Constants.Emoji.NUMBER_2,
			Constants.Emoji.NUMBER_3,
			Constants.Emoji.NUMBER_4,
			Constants.Emoji.NUMBER_5,
			Constants.Emoji.NUMBER_6,
			Constants.Emoji.NUMBER_7,
			Constants.Emoji.NUMBER_8,
			Constants.Emoji.NUMBER_9
	};
	
	private int pageSize;
	private List<String> optionsList = new ArrayList<>();
	private List<String> utilityButtons = new ArrayList<>();
	private List<String> fieldButtons = new ArrayList<>();
	
	public MenuOptions(int size) {
		setPageMaxSize(size);
		
		if(pageSize > 0) {
			for(int i = 0; i < pageSize; i++) {
				fieldButtons.add(FIELD_BUTTONS[i]);
			}
		}
	}
	
	public List<String> getFieldButtons() {
		return fieldButtons;
	}
	
	public List<Field> getPage(int pageIndex){
		List<Field> page = new ArrayList<>();
		int startIndex = pageIndex * pageSize;
		int endIndex = (startIndex + pageSize) > optionsList.size() ? optionsList.size() : startIndex + pageSize;
		
		for(int i = 0; i < endIndex - startIndex; i++) {
			String option = optionsList.get(startIndex + i);
			Field field = new Field(String.format("%d) %s", i + 1, option), "\u200b", false);
			
			page.add(field);
		}
		
		return page;
	}
	
	public int getPageMaxSize() {
		return pageSize;
	}
	
	public void setPageMaxSize(int size) {
		if(size > 9) {
			size = 9;
		} else if(size < 0) {
			size = 0;
		}
		
		pageSize = size;
	}
	
	public List<String> getUtilityButtons(){
		return utilityButtons;
	}
	
	public void addUtilityButton(String button) {
		utilityButtons.add(button);
	}
	
	public int getPageCount() {
		if(pageSize == 0) {
			return 0;
		}
		
		int count = optionsList.size() / pageSize;
		
		if((optionsList.size() % pageSize) > 0) {
			count++;
		}
		
		return count;
	}
	
	public void removeOption(int index) {		
		optionsList.remove(index);
	}
	
	public void addOption(String option) {
		optionsList.add(option);
	}
	
	public void clearOptions() {
		optionsList.clear();
	}
	
	public int size() {
		return optionsList.size();
	}
}
