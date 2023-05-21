package maestro.command.commands.music;

import java.util.List;

import maestro.Config;
import maestro.command.CommandInterface;
import maestro.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Playskip implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getChannel().asTextChannel();
		String prefix = Config.get("prefix");
		
		// this command can be abused 
		if(!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
			channel.sendMessage("you must be a moderator to be able to use this command").queue();
			return;
		}
		
		// Check for search arguments
		if(args.isEmpty()) {
			channel.sendMessage("you need to provide a youtube link or a search query\ntry `" + prefix + "play [link/query]`").queue();
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
			channel.sendMessage("you need to be in the same voice channel as me").queue();
			return;
		}
		
		String link = String.join(" ", args);
		
		if(!Play.isURL(link)) {
			link = "ytsearch:" + link + " audio ";
		}
		
		PlayerManager.getInstance().loadAndPlaySkip(channel, link);
		
	}

	@Override
	public String getName() {
		return "playskip";
	}

	@Override
	public String getHelp(String prefix) {
		return "skips the current song and plays the given song\n"+ prefix + "playskip `<link/query>`"
				+ "\nif you provide a playlist, it will add the entire playlist to the front of the queue";
	}
	
	@Override
	public List<String> getAliases() {
		return List.of("ps");
	}
	
	

}
