package core.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

//TODO: Load admins from server instance

public class Utils {
	private static ArrayList<String> admins = new ArrayList<String>();
	private static ArrayList<String> banList = new ArrayList<String>();
	
	/*
	 * Creates standard response message
	 */
	public static Message createMessage(String title, String description, boolean success){
		MessageBuilder embed = new MessageBuilder();
		Color color;
		if(success){
			color = Color.green;
		}else{
			color = Color.red;
		}
		if(title != null && !title.isEmpty()){
			embed.append(String.format("`%s`", title.replaceAll("`", "")));
		}
		if(description != null && !description.isEmpty()){
			embed.setEmbed(new EmbedBuilder().setDescription(description.replaceAll("`", "")).setColor(color).build());
		}
		
		return embed.build();
	}
	/*
	 * Creates standard response message with specified color
	 */
	public static Message createMessage(String title, String description, Color color){
		MessageBuilder embed = new MessageBuilder();
		if(title != null && !title.isEmpty()){
			embed.append(String.format("`%s`", title.replaceAll("`", "")));
		}
		if(description != null && !description.isEmpty()){
			embed.setEmbed(new EmbedBuilder().setDescription(description.replaceAll("`", "")).setColor(color).build());
		}
		return embed.build();
	}
	
	/*
	 * Creates message sans embed
	 */
	public static Message createMessage(String title){
		MessageBuilder embed = new MessageBuilder();
		embed.append(title);
		
		return embed.build();
	}
	
	/*
	 * Checks if user has admin rights
	 */
	public static boolean isAdmin(Member m){
		if(admins.contains(m.getUser().getId()) || m.hasPermission(Permission.KICK_MEMBERS)){
			return true;
		}
		return false;
	}
	
	/*
	 * Creates file
	 */
	public static void createFile(String path) {
		try{
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	/*
	 * Creates directory
	 */
	public static void createDir(String path) {
		try{
			File dir = new File(path);
			if(!dir.exists()){
				dir.mkdir();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	/*
	 * Loads list of admin ids from file
	 */
	public static void loadAdminList(){
		if (new File("app_data/admins.txt").exists()) {
			try {
				System.out.println("Loading admin list...");
				Scanner reader = new Scanner(new FileInputStream("app_data/admins.txt"));

				while (reader.hasNextLine()) {
					String id = reader.next();
					if (Pattern.matches("\\d{15,}", id)){
						admins.add(id);
					}
				}
				
				reader.close();
				System.out.println("Admin list loaded");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void loadBanList(){
		if (new File("app_data/banlist.txt").exists()) {
			try {
				System.out.println("Loading ban list...");
				Scanner reader = new Scanner(new FileInputStream("app_data/banlist.txt"));

				while (reader.hasNextLine()) {
					String id = reader.next();
					if (Pattern.matches("\\d{15,}", id)){
						banList.add(id);
					}
				}
				
				reader.close();
				System.out.println("Ban list loaded");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static boolean isBanned(String id){
		return banList.contains(id);
	}
}
