package bullybot.classfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import bullybot.classfiles.util.Functions;

public class Settings {

	private String filePath;
	
	private String pugChannel = "pugs";
	private String mumble = "N/A";
	private Integer afkTime = 120;
	private Integer dcTime = 120;
	private Integer finishTime = 60;
	private boolean randomizeCaptains = true;
	
	
	public Settings(String id){
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
			mumble = p.getProperty("mumble", mumble);
			pugChannel = p.getProperty("pugchannel", pugChannel);
			afkTime = Integer.valueOf(p.getProperty("afktime", afkTime.toString()));
			dcTime = Integer.valueOf(p.getProperty("dctime", dcTime.toString()));
			finishTime = Integer.valueOf(p.getProperty("finishtime", finishTime.toString()));
			randomizeCaptains = Boolean.valueOf(p.getProperty("randomizecaptains", String.valueOf(randomizeCaptains)));
			is.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private void createSettingsFile() {
		Functions.createFile(filePath);
		
		try{
			FileOutputStream os = new FileOutputStream(filePath);
			Properties p = new Properties();
			
			p.setProperty("mumble", mumble);
			p.setProperty("pugchannel", pugChannel);
			p.setProperty("afktime", afkTime.toString());
			p.setProperty("dctime", dcTime.toString());
			p.setProperty("finishtime", finishTime.toString());
			p.setProperty("randomizecaptains", String.valueOf(randomizeCaptains));
			p.store(os, null);
			os.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public String pugChannel(){
		return pugChannel;
	}
	
	public Integer afkTime(){
		return afkTime;
	}
	
	public Integer dcTime(){
		return dcTime;
	}
	
	public Integer finishTime(){
		return finishTime;
	}
	
	public boolean randomizeCaptains(){
		return randomizeCaptains;
	}
	
	public String mumble(){
		return mumble;
	}
}
