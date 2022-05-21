package maestro.command.commands.music;

import java.util.List;

import maestro.command.CommandInterface;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Pause implements CommandInterface {

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
		
		final boolean newPause = !manager.audioPlayer.isPaused();
		manager.audioPlayer.setPaused(newPause);
		
		if(newPause) {
			channel.sendMessage("paused").queue();
		} else {
			channel.sendMessage("resumed").queue();
		}
		
	}

	@Override
	public String getName() {
		return "pause";
	}

	@Override
	public String getHelp(String prefix) {
		return "pauses the current track, or resumes it if already paused";
	}
	
	
}
