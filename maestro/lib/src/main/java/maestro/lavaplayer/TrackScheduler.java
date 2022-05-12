package maestro.lavaplayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.TextChannel;


public class TrackScheduler extends AudioEventAdapter {
	
	public final AudioPlayer player;
	public final BlockingQueue<AudioTrack> queue;
	private TextChannel channel;
	public boolean repeating = false;
	
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.channel = null;
	}
	
	public void queue(AudioTrack track) {
		// If the track was started, there's no point in adding it to the queue.
		if(!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		}
	}
	
	public void nextTrack() {
		this.player.startTrack(this.queue.poll(), false);
		channel.sendMessage(":notes: now playing :notes:\n`" + player.getPlayingTrack().getInfo().title + "` by `" + player.getPlayingTrack().getInfo().author + "`").queue();
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			if(repeating) {
				this.player.startTrack(track.makeClone(), false);
				return;
			}
			nextTrack();
		}
	}
	
	// Needed so we can send a "now playing" message when the next track plays
	public void setChannel(TextChannel channel) {
		this.channel = channel;
	}
}
