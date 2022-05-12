package maestro;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter{
	
	private final CommandManager manager = new CommandManager();
	
	
	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // We don't want to respond to other bots, or read the message if it's not a command, or read it if it's a webhook message.
		if (event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith(Bot.prefix) || event.isWebhookMessage()) return;
		
		if(event.getMessage().getContentRaw().equalsIgnoreCase(Bot.prefix + "shutdown") && event.getAuthor().getId().equals("197524777458597890")) {
			event.getChannel().sendMessage("shutting down... bye bye :pleading_face:").queue();
			event.getJDA().shutdown();
			System.exit(0);
		}
        
        manager.handle(event);
    }
	
}
