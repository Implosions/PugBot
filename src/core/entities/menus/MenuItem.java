package core.entities.menus;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class MenuItem {
	private Message message;
	private MessageChannel channel;
	private String text;
	private String[] buttons;
	
	public MenuItem(MessageChannel channel, String text, String[] buttons){
		this.channel = channel;
		this.text = text;
		this.buttons = buttons;
		build();
	}
	
	public MenuItem(MessageChannel channel, String text, String button){
		this.channel = channel;
		this.text = text;
		buttons = new String[]{button};
		build();
	}
	
	private void build(){
		message = channel.sendMessage(text).complete();
		for(String b : buttons){
			message.addReaction(b).complete();
		}
	}
	
	public void remove(){
		message.delete().complete();
	}
	
	public MessageChannel getChannel(){
		return channel;
	}
	
	public String getText(){
		return text;
	}
}
