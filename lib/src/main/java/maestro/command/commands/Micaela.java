package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Micaela implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		
		channel.sendMessage("micaela is the most beautiful person that deserves all the love and affection. she means a lot to me and i love her so much <3").queue();
		
	}

	@Override
	public String getName() {
		return "micaela";
	}

	@Override
	public String getHelp() {
		return "tells you about micaela :D";
	}
	

}
