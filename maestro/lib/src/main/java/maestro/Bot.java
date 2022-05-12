package maestro;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
	public static JDA bot;
	public static String prefix = "m.";
	
	
	public static void main(String[] args) throws Exception {
		bot = JDABuilder.createDefault("OTczNzczMzQxMDg3MzY3MjA5.GQHHYx.QpI1XZfx-WJF9bzhvrhRE2Xj2cseV87fepLfJw")
				.enableCache(CacheFlag.VOICE_STATE)
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setActivity(Activity.playing("cards"))
				.addEventListeners(new Listener())
				.build();
		
	}
	
	
	
}
