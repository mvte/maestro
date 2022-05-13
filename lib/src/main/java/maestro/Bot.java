package maestro;

import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import maestro.database.SQLiteDataSource;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
	public static JDA bot;
	public static String prefix = Config.get("PREFIX");
	
	private Bot() throws LoginException, SQLException {
		
		bot = JDABuilder.createDefault(Config.get("token"))
			.enableCache(CacheFlag.VOICE_STATE)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("cards"))
			.addEventListeners(new Listener())
			.build();
	}
	
	
	public static void main(String[] args) throws Exception {
		new Bot();
		
	}
	
	
	
}
