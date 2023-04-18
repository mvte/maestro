package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Hello implements CommandInterface {

	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getChannel().asTextChannel();
		User user = event.getAuthor();
		
		channel.sendMessage("hello ***" + event.getGuild().getMember(user).getEffectiveName() + "***, how are you :D").queue();
	}

	public String getName() {
		return "hello";
	}

	public String getHelp(String prefix) {
		return "say hello to maestro!";
	}

}
