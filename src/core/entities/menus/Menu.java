package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Menu {
	
	private List<MenuItem> menuItemList = new ArrayList<MenuItem>();
	private MessageChannel channel;
	private Message statusMessage;
	
	
	public Menu(MessageChannel c){
		this.channel = c;
	}
	
	public MenuItem getMenuItem(String id){
		for(MenuItem m : menuItemList){
			if (m.getId().equals(id)){
				return m;
			}
		}
		return null;
	}
	
	public void createMenuItem(String text, String[] buttons){
		menuItemList.add(new MenuItem(channel, text, buttons));
	}
	
	public void createMenuItem(String text, String button){
		menuItemList.add(new MenuItem(channel, text, button));
	}
	
	public void createStatusMessage(String text){
		statusMessage = channel.sendMessage(text).complete();
	}
	
	public void editStatusMessage(String text){
		statusMessage.editMessage(text).complete();
	}
	
	public MessageChannel getChannel(){
		return channel;
	}
	
	public void deleteMenuItems(){
		menuItemList.forEach((mi) -> mi.remove());
		menuItemList.clear();
	}
	
	public void deleteStatusMessage(){
		statusMessage.delete().complete();
	}
}
