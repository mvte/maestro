package maestro.command.commands.music;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import maestro.command.CommandInterface;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Queue implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getTextChannel();
		final Member self = event.getGuild().getSelfMember();
		final GuildVoiceState selfVS = self.getVoiceState();
		
		if(!selfVS.inAudioChannel()) {
			channel.sendMessage("i'm not in a voice channel").queue();
		}
		
		final Member member = event.getMember();
		final GuildVoiceState memberVS = member.getVoiceState();
		
		if(!memberVS.inAudioChannel()) {
			channel.sendMessage("you need to be in a voice channel").queue();
		}
		
		if(!memberVS.getChannel().equals(selfVS.getChannel())) {
			channel.sendMessage("you need to be in the same voice channel as me").queue();
		}
		
		final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		final BlockingQueue<AudioTrack> queue = manager.scheduler.queue;
		
		if(queue.isEmpty()) {
			channel.sendMessage("there are no songs in the queue").queue();
			return;
		}
		
		EmbedBuilder embed = new EmbedBuilder().setTitle("song queue");
		
		// Message embeds have a limit of 25 songs. Once I have iterated 25 times, I will break out of the loop
		int i = 1;
		for(AudioTrack track : queue) {
			if(i == 26) {
				if(queue.size() != 25)
					embed.setFooter("+" + (queue.size()-25) + " more songs", null);
				break;
			}
			
			embed.addField(i + ". " + track.getInfo().title, track.getInfo().author + " [`" + formatTime(track.getDuration()) + "`]", false);
			i++;
		}
		
		channel.sendMessageEmbeds(embed.build()).queue();
		
		
	}
	
	/**
	 * Takes a duration of time in milliseconds and transforms it into a string of format minutes:seconds
	 * @param time in milliseconds
	 * @return String of format mm:ss
	 */
	private String formatTime(long ms) {
		final long min = ms / TimeUnit.MINUTES.toMillis(1);
		final long sec = ms % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
		
		return String.format("%02d:%02d", min, sec);
	}

	@Override
	public String getName() {
		return "queue";
	}

	@Override
	public String getHelp(String prefix) {
		return "displays all the songs in the queue";
	}

}
