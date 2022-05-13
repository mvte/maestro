package maestro;

import maestro.database.DatabaseManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter{
	
	private final CommandManager manager = new CommandManager();
	
	
	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // We don't want to respond to other bots, or read the message if it's not a command, or read it if it's a webhook message.
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
	
}
