package maestro.command.commands.music;

import java.util.List;

import maestro.Bot;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Leave implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getChannel().asTextChannel();
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
		
		Bot.clean(guild);
		
		final VoiceChannel selfChannel = selfVoiceState.getChannel().asVoiceChannel();
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
	
	@Override
	public List<String> getAliases() {
		return List.of("l");
	}
	
}
