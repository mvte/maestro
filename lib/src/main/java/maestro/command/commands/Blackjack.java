package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Blackjack implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		
		channel.sendMessage("coming soon ;)").queue();
	}

	@Override
	public String getName() {
		return "blackjack";
	}

	@Override
	public String getHelp() {
		return "usage: m.blackjack `[command]`\n" +
					"`start` begins a game of blackjack";
	}

}
