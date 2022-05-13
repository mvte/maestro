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

public class Repeat implements CommandInterface {

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
		final AudioPlayer player = manager.audioPlayer;
		
		if(player.getPlayingTrack() == null) {
			channel.sendMessage("i'm not playing anything").queue();
			return;
		}
		
		final boolean newRep = !manager.scheduler.repeating;
		manager.scheduler.repeating = newRep;
		AudioTrack track = manager.audioPlayer.getPlayingTrack();
		
		if(newRep) {
			channel.sendMessage(":notes: repeating :notes:\n`" + track.getInfo().title + "` by `" + track.getInfo().author + "`").queue();
		} else {
			channel.sendMessage("no longer repeating").queue();
		}
		
	}

	@Override
	public String getName() {
		return "repeat";
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return "repeats the currently playing song. if a song is already repeating, stops repeating";
	}

	
}
