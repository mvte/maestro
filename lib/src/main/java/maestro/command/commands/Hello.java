package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Hello implements CommandInterface {

	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		User user = event.getAuthor();
		String greet;
		if(event.isFromGuild()) {
			greet = "hello ***" + event.getGuild().getMember(user).getEffectiveName() + "***, how are you :D";
		}
		else {
			greet = "hello ***" + user.getName() + "***, how are you :D";
		}
		channel.sendMessage(greet).queue();

	}

	public String getName() {
		return "hello";
	}

	public String getHelp(String prefix) {
		return "say hello to maestro!";
	}

}
