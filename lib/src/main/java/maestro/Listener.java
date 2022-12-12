package maestro;

import maestro.blackjack.BlackjackManager;
import maestro.database.DatabaseManager;
import maestro.lavaplayer.GuildMusicManager;
import maestro.lavaplayer.PlayerManager;
import maestro.model.UserModel;
import maestro.model.UserModelDatabase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Listener extends ListenerAdapter{
	
	private final CommandManager manager = new CommandManager();
	
	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // We don't want to respond to other bots or read it if it's a webhook message.
		if (event.getAuthor().isBot() || event.isWebhookMessage()) return;
		
		final long guildId = event.getGuild().getIdLong();
		String prefix = PrefixManager.PREFIXES.computeIfAbsent(guildId, DatabaseManager.INSTANCE::getPrefix);

		if(event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("owner_id"))) {
			event.getChannel().sendMessage("shutting down... bye bye :pleading_face:").complete();
			System.out.println("shutting down");
			event.getJDA().shutdown();
			System.exit(0);
		}

		boolean bool = UserModelDatabase.getInstance().addUserIfNotExist(new UserModel(event.getAuthor().getIdLong()));

		if(event.getMessage().getContentRaw().startsWith(prefix)) {
			manager.handle(event, prefix);
		}
    }
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		
		if(event.getComponentId().startsWith("blackjack:")) {
			BlackjackManager.getInstance().handleButtonPress(event);
		}
			
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		// We want to leave the voice channel if there's no one else in it
		Guild guild = event.getGuild();
		Member self = guild.getSelfMember();
		
		// if the person that left was us (force disconnect)
		if(!self.getVoiceState().inAudioChannel()) {
			Bot.clean(guild);
			return;
		}
		
		// if we are the last person in the call
		if(self.getVoiceState().getChannel().getMembers().size() == 1 && self.getVoiceState().getChannel().getMembers().get(0).equals(self)) {
			Bot.clean(guild);
			return;
		}
		
	}
}
