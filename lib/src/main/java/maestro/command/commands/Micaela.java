package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Micaela implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getChannel().asTextChannel();
		
		channel.sendMessage("micaela is the most beautiful person that deserves all the love and affection. she means a lot to me and i love her so much <3").queue();
		
	}

	@Override
	public String getName() {
		return "micaela";
	}

	@Override
	public String getHelp(String prefix) {
		return "tells you about micaela :D";
	}
	
	@Override
	public List<String> getAliases() {
		return List.of("micaelaconcepcion", "micaelaventuraconcepcion", "micaelamarzan", "prettiestgirlever", "mica", "micmic", "bbygirl");
	}

}
