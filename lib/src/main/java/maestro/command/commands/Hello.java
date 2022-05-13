package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Hello implements CommandInterface {

	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		User user = event.getAuthor();
		
		channel.sendMessage("hello ***" + user.getName() + "***, how are you :D").queue();
	}

	public String getName() {
		return "hello";
	}

	public String getHelp() {
		return "say hello to maestro!";
	}

}
