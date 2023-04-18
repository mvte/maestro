package maestro.command.commands.music;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import maestro.PrefixManager;
import maestro.command.CommandInterface;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Remove implements CommandInterface {
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getChannel().asTextChannel();
		final Member self = event.getGuild().getSelfMember();
		final GuildVoiceState selfVS = self.getVoiceState();
		final String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
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
		
		if(manager.scheduler.queue.isEmpty()) {
			channel.sendMessage("there is nothing in the queue to remove").queue();
			return;
		}
		
		if(args.isEmpty()) {
			channel.sendMessage("you need to provide the track number to remove\nsee `" + prefix + "help remove`").queue();
			return;
		}
		
		String nStr = args.get(0);
		
		if(nStr.equals("all")) {
			manager.scheduler.queue.removeAll(manager.scheduler.queue);
			channel.sendMessage("removed all songs from the queue").queue();
			return;
		}
		
		int n;
		try {
			n = Integer.parseInt(nStr);
		} catch (Exception e) {
			channel.sendMessage("must enter an integer").queue();
			return;
		}
		
		if(n > manager.scheduler.queue.size()) {
			channel.sendMessageFormat("cannot remove track %d (there are only %d tracks in the queue)", n, manager.scheduler.queue.size()).queue();
			return;
		}
		
		if(n < 0) {
			channel.sendMessage("integer must be positive").queue();
			return;
		}
		
		int i = 1;
		for(AudioTrack track : manager.scheduler.queue) {
			if(i == n) {
				manager.scheduler.queue.remove(track);
				channel.sendMessageFormat("removing track\n`%s` by `%s`", track.getInfo().title, track.getInfo().author).queue();
				return;
			}
			i++;
		}
		
	}

	@Override
	public String getName() {
		return "remove";
	}
	
	@Override
	public String getHelp(String prefix) {
		return "removes the *nth* track in the queue\n" + prefix + "remove `<n>`\nyou can also use " + prefix + "remove `all` to clear the queue";
	}
	
	public List<String> getAliases() {
		return List.of("rm");
	}

}
