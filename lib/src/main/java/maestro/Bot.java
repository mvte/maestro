package maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
	public static JDA bot;
	public static String prefix = Config.get("PREFIX");
	public static EventWaiter waiter = new EventWaiter();
	public static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	public static final String db_url = "jdbc:mysql://db:3306/maestro";
	public static final String db_user = "root";

	
	private Bot() throws LoginException, SQLException {
		bot = JDABuilder.createDefault(Config.get("token"))
			.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.setChunkingFilter(ChunkingFilter.ALL)
			.enableCache(CacheFlag.VOICE_STATE)
			.setStatus(OnlineStatus.ONLINE)
			.setActivity(Activity.playing("cards"))
			.addEventListeners(new Listener(), waiter)
			.build();

	}
	
	
	public static void main(String[] args) throws Exception {
		new Bot();
	}
	
	/**
	 * Cleans a guild's audio state (i.e. closes audio connection, destroys player, clears future)
	 * @param guild The guild who's audio state is to be cleaned
	 */
	public static void clean(Guild guild) {
		final AudioManager audioManager = guild.getAudioManager();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
		
		audioManager.closeAudioConnection();
		musicManager.audioPlayer.destroy();
		if(musicManager.scheduler.future != null)
			musicManager.scheduler.future.cancel(false);
	}
	
	
	
}
