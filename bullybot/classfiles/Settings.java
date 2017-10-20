package bullybot.classfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import bullybot.classfiles.util.Functions;

public class Settings {
	
	//private String id;
	private String filePath;
	
	private String pugChannel;
	
	public Settings(String id){
		//this.id = id;
		this.filePath = String.format("%s/%s/%s", "app_data", id, "settings.cfg");
		if(!new File(filePath).exists()){
			createSettingsFile();
		}
		loadSettingsFile();
	}

	private void loadSettingsFile() {
		try{
			FileInputStream is = new FileInputStream(filePath);
			Properties p = new Properties();
			p.load(is);
			pugChannel = p.getProperty("pugchannel", "pugs");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private void createSettingsFile() {
		Functions.createFile(filePath);
		
		try{
			FileOutputStream os = new FileOutputStream(filePath);
			Properties p = new Properties();
			
			p.setProperty("pugchannel", "pugs");
			p.store(os, null);
			os.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public String getPugChannelName(){
		return pugChannel;
	}
}
