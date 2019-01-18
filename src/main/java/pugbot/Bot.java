package pugbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import okhttp3.OkHttpClient;
import pugbot.core.Database;
import pugbot.core.EventHandler;

// Bot driver

public class Bot {
	
	private final static String propertiesFilepath = "app_data/.properties";
	private final static String key = "OAuthToken";
	
	public static void main(String[] args) throws LoginException, IOException {
		Utils.createDir("app_data");
		
		Properties properties = new Properties();
		
		try(FileInputStream is = new FileInputStream(propertiesFilepath)){
			properties.load(is);
		} catch(FileNotFoundException ex) {
			System.out.println(".properties file not found! Generating new file");
			properties.setProperty(key, new String());
			
			try(FileOutputStream os = new FileOutputStream(propertiesFilepath)){
				properties.store(os, "--- PugBot properties ---");
			}
		}
		
		String token = properties.getProperty(key);
		
		Database.createConnection();
		OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
				.readTimeout(60, TimeUnit.SECONDS)
				.connectTimeout(60, TimeUnit.SECONDS)
				.writeTimeout(60, TimeUnit.SECONDS);
		
		JDA jda = new JDABuilder(AccountType.BOT)
				.setToken(token)
				.setHttpClientBuilder(httpBuilder)
				.build();
		
		jda.setAutoReconnect(true);
		jda.addEventListener(new EventHandler());
	}
}
