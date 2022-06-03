package maestro.command.commands.music;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import maestro.command.CommandInterface;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowPlaying implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getTextChannel();
		final Member self = event.getGuild().getSelfMember();
		final GuildVoiceState selfVS = self.getVoiceState();
		
		if(!selfVS.inAudioChannel()) {
			channel.sendMessage("i'm not in a voice channel").queue();
			return;
		}
		
		final Member member = event.getMember();
		final GuildVoiceState memberVS = member.getVoiceState();
		
		if(!memberVS.inAudioChannel()) {
			channel.sendMessage("you need to be in a voice channel").queue();
			return;
		}
		
		if(!memberVS.getChannel().equals(selfVS.getChannel())) {
			channel.sendMessage("you need to be in the same voice channel as me").queue();
			return;
		}
		
		
		final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		final AudioPlayer player = manager.audioPlayer;
		
		if(player.getPlayingTrack() == null) {
			channel.sendMessage("i'm not playing anything").queue();
			return;
		}
		
		AudioTrack track = player.getPlayingTrack();
		
		String curr = Queue.formatTime(track.getPosition());
		String tot = Queue.formatTime(track.getDuration());
		
		String time = String.format("[`%s` / `%s`]", curr, tot);
		
		channel.sendMessage(":notes: now playing :notes:\n`" + track.getInfo().title + "` by `" + track.getInfo().author + "`\n" + time).queue();
		
	}

	@Override
	public String getName() {
		return "np";
	}

	@Override
	public String getHelp(String prefix) {
		return "displays the song that is currently playing";
	}
	
	@Override
	public List<String> getAliases() {
		return List.of("nowplaying");
	}
	
}
