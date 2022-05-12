package maestro.command;

import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// All commands should must implement this
public interface CommandInterface {
	
	void handle(MessageReceivedEvent event, List<String> args);
	
	String getName();
	
	// Returns the help message when b.[command] help is called
	String getHelp();
	
	

}
