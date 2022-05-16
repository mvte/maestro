package maestro;

import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
	public static JDA bot;
	public static String prefix = Config.get("PREFIX");
	public static EventWaiter waiter = new EventWaiter();
	
	private Bot() throws LoginException, SQLException {
		
		bot = JDABuilder.createDefault(Config.get("token"))
			.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.ALL)
			.enableCache(CacheFlag.VOICE_STATE)
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setActivity(Activity.playing("cards"))
			.addEventListeners(new Listener(), waiter)
			.build();
	}
	
	
	public static void main(String[] args) throws Exception {
		new Bot();
		
	}
	
	
	
}
