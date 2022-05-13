package maestro.command.commands.music;

import java.util.List;

import maestro.command.CommandInterface;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Stop implements CommandInterface {

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
		manager.scheduler.player.stopTrack();
		manager.scheduler.queue.clear();
		channel.sendMessage("maestro player has stopped").queue();
	}

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getHelp(String prefix) {
		return "stops current song and clears the queue";
	}

}
