package core.entities.menus;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Menu {
	
	private List<MenuItem> menuItemList = new ArrayList<MenuItem>();
	private MessageChannel channel;
	private Message statusMessage = null;
	private MenuStatus menuStatus = null;
	
	public enum MenuStatus{
		INITIALIZING,
		COMPLETE,
		ABORTED;
	}
	
	public Menu(MessageChannel c){
		this.channel = c;
		this.menuStatus = MenuStatus.INITIALIZING;
	}
	
	public void changeMenuStatus(MenuStatus newStatus){
		menuStatus = newStatus;
	}
	
	public MenuItem getMenuItem(String id){
		for(MenuItem m : menuItemList){
			if (m.getId().equals(id)){
				return m;
			}
		}
		return null;
	}
	
	public MenuItem getMenuItem(Integer index){
		return menuItemList.get(index);
	}
	
	public MenuItem getMenuItemByText(String text){
		for(MenuItem m : menuItemList){
			if (m.getText().equals(text)){
				return m;
			}
		}
		return null;
	}
	
	public void createMenuItem(String text, String[] buttons){
		if(menuStatus != MenuStatus.ABORTED){
			menuItemList.add(new MenuItem(channel, text, buttons));
		}
	}
	
	public void createMenuItem(String text, String button){
		if(menuStatus != MenuStatus.ABORTED){
			menuItemList.add(new MenuItem(channel, text, button));
		}
	}
	
	public void createStatusMessage(String text){
		if(menuStatus != MenuStatus.ABORTED){
			statusMessage = channel.sendMessage(text).complete();
		}
	}
	
	public void editStatusMessage(String text){
		if(menuStatus != MenuStatus.ABORTED){
			statusMessage.editMessage(text).complete();
		}
	}
	
	public MessageChannel getChannel(){
		return channel;
	}
	
	public void deleteMenuItems(){
		menuItemList.forEach((mi) -> mi.remove());
		menuItemList.clear();
	}
	
	public void deleteMenuItem(MenuItem mi){
		mi.remove();
		menuItemList.remove(mi);
	}
	
	public void deleteStatusMessage(){
		if(statusMessage != null){
			statusMessage.delete().complete();
		}
	}
}
