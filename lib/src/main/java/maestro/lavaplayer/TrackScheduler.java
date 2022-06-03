package maestro.lavaplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;


public class TrackScheduler extends AudioEventAdapter {
	
	public final AudioPlayer player;
	public final BlockingQueue<AudioTrack> queue;
	
	private TextChannel channel;
	public boolean repeating = false;
	private int errorCount = 0;
	public double endTime = -1;		//special value (for if the bot joins and hasn't started playing anything)
	public ScheduledFuture<?> future;
	public final Inactivity inactivity = new Inactivity();
	
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.channel = null;
	}
	
	public void queue(AudioTrack track) {
		endTime = 0;
		// If the track was started, there's no point in adding it to the queue.
		if(!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		}
	}
	
	public void nextTrack() {
		this.player.startTrack(queue.poll(), false);
		channel.sendMessage(":notes: now playing :notes:\n`" + player.getPlayingTrack().getInfo().title + "` by `" + player.getPlayingTrack().getInfo().author + "`").queue();
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {	
		if(endReason == AudioTrackEndReason.FINISHED) {
			errorCount = 0;
		}
		
		endTime = System.currentTimeMillis();
		
		if(endReason.mayStartNext) {
			if(repeating) {
				this.player.startTrack(track.makeClone(), false);
				return;
			}
			nextTrack();
		}
			
	}
	
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		if(errorCount > 5) {
			System.out.println("too many errors, not trying again");
			errorCount = 0;
			exception.printStackTrace();
			return;
		}
		
		errorCount++;
		System.out.println("error playing track " + track.getInfo().title + " in server "  + channel.getGuild().getName() + " (" + errorCount + ")");
	    
		player.startTrack(track.makeClone(), false);
	  }
	
	// Needed so we can send a "now playing" message when the next track plays
	public void setChannel(TextChannel channel) {
		this.channel = channel;
	}
	
	/**
	 * Puts all the tracks in the List to the front of the queue, and skips the currently playing song.
	 * @param tracks the tracks to be placed at the front of the queue
	 */
	public void playSkip(List<AudioTrack> tracks) {
		BlockingQueue<AudioTrack> nq = new LinkedBlockingQueue<>();
		nq.addAll(tracks);
		
		for(AudioTrack track : queue) {
			nq.offer(track);
		}
		
		this.queue.clear();
		this.queue.addAll(nq);
		
		nextTrack();
	}

	/**
	 * Adds a single track to the front of the queue, and skips the currently playing song. 
	 * @param track the track to be played immediately
	 */
	public void playSkip(AudioTrack track) {
		playSkip(List.of(track));
	}
	
	private class Inactivity implements Runnable {

		@Override
		/**
		 * Disconnects the bot from a server if they haven't been playing anything for more than 4.833 minutes. 
		 */
		public void run() {
			final Guild guild = channel.getGuild();
			final AudioManager audioManager = guild.getAudioManager();
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
			
			boolean timeLimitPassed = (System.currentTimeMillis() - endTime) >= 290000 || endTime == -1;
			boolean notPlaying = musicManager.audioPlayer.getPlayingTrack() == null && !musicManager.audioPlayer.isPaused();
			
			if(timeLimitPassed && notPlaying) {
				audioManager.closeAudioConnection();
				musicManager.audioPlayer.destroy();
				channel.sendMessage("leaving due to inactivity").queue();
				future.cancel(false);
			}
		}
		
	}
	
}
