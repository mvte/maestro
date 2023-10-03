package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Coinflip implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getChannel().asTextChannel();
		
		double rand = Math.random();
		
		String result = rand > 0.5 ? "heads" : "tails";
		
		channel.sendMessage(result).queue();
		
	}

	@Override
	public String getName() {
		return "coinflip";
	}

	@Override
	public String getHelp(String prefix) {
		return "flips a coin";
	}
	
	@Override 
	public List<String> getAliases() {
		return List.of("flipcoin", "cf", "fc");
	}
	
}
