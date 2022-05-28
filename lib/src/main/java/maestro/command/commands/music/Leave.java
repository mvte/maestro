package maestro.command.commands.music;

import java.util.List;

import maestro.command.CommandInterface;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Leave implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final MessageChannel channel = event.getChannel();
		final Member member = event.getMember();
		
		if(member == null) {
			channel.sendMessage("you must be in a server!").queue();
			return;
		}
		
		final Guild guild = event.getGuild();
		final Member self = guild.getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		
		if(!selfVoiceState.inAudioChannel()) {
			channel.sendMessage("i'm not in any voice channel!").queue();
			return;
		}
		
		final AudioManager audioManager = guild.getAudioManager();
		final AudioChannel selfChannel = selfVoiceState.getChannel();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
		
		audioManager.closeAudioConnection();
		musicManager.audioPlayer.destroy();
		channel.sendMessage("leaving :loud_sound: **" + selfChannel.getName() + "**").queue();
		
	}

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public String getHelp(String prefix) {
		return "have maestro leave your voice channel";
	}

}
