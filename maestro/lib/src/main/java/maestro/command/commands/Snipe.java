package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Snipe implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		
		channel.sendMessage("coming soon ;)").queue();
	}

	@Override
	public String getName() {
		return "snipe";
	}

	@Override
	public String getHelp() {
		return "the bot will send you a message when a section for a sniped class has opened\nusage: m.snipe `<index>`";
	}
	
}
