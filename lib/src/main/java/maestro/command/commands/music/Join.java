package maestro.command.commands.music;

import java.util.List;
import java.util.concurrent.TimeUnit;

import maestro.Bot;
import maestro.command.CommandInterface;
import maestro.lavaplayer.PlayerManager;
import maestro.lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Join implements CommandInterface {
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getTextChannel();
		final Member member = event.getMember();
		
		if(member == null) {
			channel.sendMessage("you must be in a server!").queue();
			return;
		}
		
		final Guild guild = event.getGuild();
		final Member self = guild.getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		
		if(!self.hasPermission(Permission.VOICE_CONNECT)) {
			channel.sendMessage("i don't have permission to connect to a voice channel");
			return;
		}
		
		if(selfVoiceState.inAudioChannel()) {
			channel.sendMessage("i'm already in a voice channel!").queue();
			return;
		}
		
		if(!member.getVoiceState().inAudioChannel()) {
			channel.sendMessage("you must be in a voice channel!").queue();
			return;
		}
		
		final AudioManager audioManager = guild.getAudioManager();
		final AudioChannel memberChannel = member.getVoiceState().getChannel();
		final TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(guild).scheduler;
		
		scheduler.setChannel(channel);
		audioManager.openAudioConnection(memberChannel);
		channel.sendMessage("connecting to :loud_sound: **" + memberChannel.getName() + "**").queue();
		audioManager.setSelfDeafened(true);
	
		scheduler.future = Bot.service.scheduleAtFixedRate(scheduler.inactivity, 3, 2, TimeUnit.MINUTES);
	}

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public String getHelp(String prefix) {
		return "have maestro join your voice channel\n_you must be in a voice channel, and maestro must not be in another voice channel_";
	} 
	
	

}
