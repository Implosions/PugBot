package core.commands;

import java.util.ArrayList;
import java.util.List;
import core.Constants;
import core.entities.Server;
import core.entities.Settings;
import core.exceptions.BadArgumentsException;
import core.exceptions.InvalidUseException;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdSettings extends Command{
	
	private List<String> settingsList = new ArrayList<String>();
	
	public CmdSettings(){
		this.name = Constants.SETTINGS_NAME;
		this.helpMsg = Constants.SETTINGS_HELP;
		this.description = Constants.SETTINGS_DESC;
		this.adminRequired = true;
		this.pugCommand = false;
		
		settingsList.add("pugchannel");
		settingsList.add("mumble");
		settingsList.add("afktime");
		settingsList.add("dctime");
		settingsList.add("finishtime");
		settingsList.add("minnumberofgames");
		settingsList.add("randomizecaptains");
		settingsList.add("snakepick");
		settingsList.add("postteams");
	}
	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length == 0){
			String settingsInfo = "";
			for(String setting : settingsList){
				settingsInfo += String.format("%s = %s%n", setting, getSettingInfo(setting, server.getSettings()));
			}
			
			this.response = Utils.createMessage("Settings", settingsInfo, true);
		}else{
			String setting = args[0].toLowerCase();
			if(settingsList.contains(setting)){
				if(args.length == 1){
					String s = String.format("%s = %s%n", setting, getSettingInfo(setting, server.getSettings()));
					this.response = Utils.createMessage("Settings", s, true);
				}else if(args.length == 2){
					setSetting(setting, args[1], server.getSettings());
					this.response = Utils.createMessage("Settings", "Setting updated", true);
				}else{
					throw new BadArgumentsException();
				}
			}else{
				throw new InvalidUseException(String.format("%s is not a valid setting", setting));
			}
		}
		System.out.println(success());
		
		return response;
	}
	
	private String getSettingInfo(String setting, Settings settings){
		switch(setting){
		case "pugchannel": return settings.pugChannel();
		case "mumble": return settings.mumble();
		case "afktime": return settings.afkTime().toString() + " minutes";
		case "dctime": return settings.dcTime().toString() + " seconds";
		case "finishtime": return settings.finishTime().toString() + " seconds";
		case "minnumberofgames": return settings.minNumberOfGames().toString();
		case "randomizecaptains": return String.valueOf(settings.randomizeCaptains());
		case "snakepick": return String.valueOf(settings.snakePick());
		case "postteams": return String.valueOf(settings.postTeams());
		default: return null;
		}
	}
	
	private void setSetting(String setting, String value, Settings settings){
		if(setting.equals("pugchannel") || setting.equals("mumble")){
			settings.setProperty(setting, value);
		}else if(setting.equals("afktime") || setting.equals("dctime") || setting.equals("finishtime") || setting.equals("minnumberofgames")){
			try{
				int num = Integer.valueOf(value);
				if(num > 0){
					settings.setProperty(setting, num);
				}else{
					throw new InvalidUseException("Value must be greater than 0");
				}
			}catch(Exception ex){
				throw new BadArgumentsException();
			}
		}else if(setting.equals("randomizecaptains") || setting.equals("snakepick") || setting.equals("postteams")){
			try{
				settings.setProperty(setting, Boolean.valueOf(value));
			}catch(Exception ex){
				throw new BadArgumentsException();
			}
		}
		settings.loadSettingsFile();
	}
}
