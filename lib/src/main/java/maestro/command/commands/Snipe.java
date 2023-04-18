package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Snipe implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		PrivateChannel channel = event.getChannel().asPrivateChannel();
		
		channel.sendMessage("coming soon ;)").queue();
	}

	@Override
	public String getName() {
		return "snipe";
	}

	@Override
	public String getHelp(String prefix) {
		return "the bot will send you a message when a section for a sniped class has opened\nusage: m.snipe `<index>`";
	}
	
}
