package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ping implements CommandInterface {

	public void handle(MessageReceivedEvent event, List<String> args) {
		JDA bot = event.getJDA();
		
		bot.getRestPing().queue( (time) ->
	    	event.getChannel().sendMessageFormat("pong! `%d ms`", time).queue()
		);
		
		
	}

	public String getName() {
		return "ping";
	}

	public String getHelp(String prefix) {
		return "displays \"pong!\" along with the maestro's ping to discord's servers";
	}
	

}
