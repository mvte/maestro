package maestro.command.commands.music;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import maestro.command.CommandInterface;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Play implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getTextChannel();
		
		// Check for search arguments
		if(args.isEmpty()) {
			channel.sendMessage("you need to provide a youtube link or search query!\ntry `m.play [link/query]`").queue();
			return;
		}
		
		Member self = event.getGuild().getSelfMember();
		GuildVoiceState selfVoiceState = self.getVoiceState();
		final Member member = event.getMember();
		final GuildVoiceState memberState = member.getVoiceState();
		
		// Joins the channel that the member is in if the bot is not already any in a channel. Otherwise, stay in current channel.
		if(!selfVoiceState.inAudioChannel()) {
			Join j = new Join();
			j.handle(event, args);
		} else if(!memberState.getChannel().equals(selfVoiceState.getChannel())) {
			channel.sendMessage("you need to be in the same voice channel as me.").queue();
			return;
		}
		
		String link = String.join(" ", args);
		
		if(!isURL(link)) {
			link = "ytsearch:" + link;
		}
		
		// Create PlayerManager and use it to load the song. 
		PlayerManager.getInstance()
			.loadAndPlay(channel, link);
	}

	@Override
	public String getName() {
		return "play";
	}

	@Override
	public String getHelp() {
		return "plays a song given a youtube link or search query\nusage: m.play `[link/query]`\n";
	}
	
	/**
	 * Checks if a string is a URL by attempting to create a URI object with it
	 * @param url
	 * @return true if URI object is created (therefore the string is a URL)
	 */
	private boolean isURL(String url) {
		try {
			new URI(url);
			return true;	
		} catch (URISyntaxException e) {
			return false;
		}
	}

}
