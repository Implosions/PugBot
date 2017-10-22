
import java.util.concurrent.TimeUnit;

import bullybot.classfiles.EventHandler;

import bullybot.classfiles.Info;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import okhttp3.OkHttpClient;

public class Bot{
	
	public static void main(String[] args) {
		try{
			OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.MINUTES);
			JDA jda = new JDABuilder(AccountType.BOT).setToken(Info.TOKEN).setHttpClientBuilder(httpBuilder).buildBlocking();
			jda.setAutoReconnect(true);
			//jda.getPresence().setGame(Game.of("Videogames"));
			jda.addEventListener(new EventHandler(jda));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
