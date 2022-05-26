package maestro;

import maestro.blackjack.BlackjackManager;
import maestro.database.DatabaseManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
        
		if(event.getMessage().getContentRaw().startsWith(prefix))
			manager.handle(event, prefix);
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
		Member self = event.getGuild().getSelfMember();
		
		if(!self.getVoiceState().inAudioChannel()) {
			return;
		}
		
		if(self.getVoiceState().getChannel().getMembers().size() == 1 && self.getVoiceState().getChannel().getMembers().get(0).equals(self)) {
			event.getGuild().getAudioManager().closeAudioConnection();
		}
		
	}
	
}
