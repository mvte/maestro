package maestro.lavaplayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;


public class TrackScheduler extends AudioEventAdapter {
	
	public final AudioPlayer player;
	public final BlockingQueue<AudioTrack> queue;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}
	
	public void queue(AudioTrack track) {
		// If the track was started, then we're just going to add it to the queue.
		if(!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		}
	}
	
	public void nextTrack() {
		this.player.startTrack(this.queue.poll(), false);
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			nextTrack();
		}
	}
}
