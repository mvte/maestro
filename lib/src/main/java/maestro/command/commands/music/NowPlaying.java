package maestro.command.commands.music;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

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
		
		if(player.getPlayingTrack() != null) {
			channel.sendMessage(":notes: now playing :notes:\n`" + player.getPlayingTrack().getInfo().title + "` by `" + player.getPlayingTrack().getInfo().author + "`").queue();
			return;
		} else {
			channel.sendMessage("i'm not playing anything").queue();
		}
		
		
		
		
	}

	@Override
	public String getName() {
		return "np";
	}

	@Override
	public String getHelp(String prefix) {
		return "displays the song that is currently playing";
	}
	
}
