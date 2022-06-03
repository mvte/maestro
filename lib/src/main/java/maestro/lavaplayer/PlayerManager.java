package maestro.lavaplayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Manages music players across servers
 * There's only one instance of this object (access by PlayerManager.[data])
 */
public class PlayerManager {
	
	private static PlayerManager INSTANCE;
	
	private final Map<Long, GuildMusicManager> musicManagers;
	private final AudioPlayerManager audioPlayerManager;
	
	/**
	 * Ideally should be run once for every JDA instance of maestro
	 */
	public PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();
		
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}
	
	/**
	 * Returns a guild's music manager. If the music manager for a guild does not exist, then it will create one.
	 * @param guild
	 * @return A GuildMusicManager specific to that guild
	 */
	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
				final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
				guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
				
				return guildMusicManager;
		});
	}
	
	/**
	 * Load a track and play it in a voice channel
	 * @param channel
	 * @param trackURL
	 */
	public void loadAndPlay(TextChannel channel, String trackURL) {
		final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
		this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
			//defining an AudioLoadResultHandler
			@Override
			public void trackLoaded(AudioTrack track) {
				
				// If there is no song playing, display the "now playing message"
				if(musicManager.audioPlayer.getPlayingTrack() != null) {
					channel.sendMessage(":notes: adding to queue :notes:\n`" + 
						track.getInfo().title + "` by `" + track.getInfo().author + "`").queue();
				} else {
					channel.sendMessage(":notes: now playing :notes:\n`" + track.getInfo().title + "` by `" + track.getInfo().author + "`").queue();
				}
				
				musicManager.scheduler.queue(track);
				
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				final List<AudioTrack> tracks = playlist.getTracks();
				
				if(playlist.isSearchResult()) {
					trackLoaded(tracks.get(0));
					return;
				}
				
				channel.sendMessage(":notes: adding to queue :notes:\n" + String.valueOf(tracks.size()) + " tracks from  playlist `" + playlist.getName() + "`").queue();
				
				for(final AudioTrack track : tracks) {
					musicManager.scheduler.queue(track);
				}
				
				if(musicManager.audioPlayer.getPlayingTrack().equals(tracks.get(0)))
					channel.sendMessage(":notes: now playing :notes:\n`" + tracks.get(0).getInfo().title + "` by `" + tracks.get(0).getInfo().author + "`").queue();
			}

			@Override
			public void noMatches() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				System.out.println("uh oh, something went wrong D:\n" + exception.getMessage().toLowerCase());
				exception.printStackTrace();
				
			}
			
		});
	}
	
	public void loadAndPlaySkip(TextChannel channel, String trackURL) {
		final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
		this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				if(musicManager.audioPlayer.getPlayingTrack() != null)
					channel.sendMessage("skipping...").queue();
				channel.sendMessage("now playing `" + track.getInfo().title + "` by `" + track.getInfo().author + "`");
				
				musicManager.scheduler.playSkip(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				
				if(playlist.isSearchResult()) {
					trackLoaded(playlist.getTracks().get(0));
					return;
				}
				
				if(musicManager.audioPlayer.getPlayingTrack() != null)
					channel.sendMessage("skipping...").queue();
				channel.sendMessage("adding to front of queue `" + playlist.getTracks().size() + "` tracks from playlist `" + playlist.getName() + "`").queue();
				
				musicManager.scheduler.playSkip(playlist.getTracks());
			}

			@Override
			public void noMatches() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				// TODO Auto-generated method stub
				
			}

		});
	}
	
	/**
	 * @return The global PlayerManager instance
	 */
	public static PlayerManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new PlayerManager();
		}
		
		
		return INSTANCE;
	}
	
}
