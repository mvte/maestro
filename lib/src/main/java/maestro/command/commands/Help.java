package maestro.command.commands;

import java.awt.Color;
import java.util.List;

import maestro.Bot;
import maestro.CommandManager;
import maestro.PrefixManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help implements CommandInterface{
	
	private final CommandManager manager;
	
	public Help(CommandManager manager) {
		this.manager = manager;
	}
	
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		EmbedBuilder embed = new EmbedBuilder().setColor(new Color(0x000000));
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		// Help command was invoked without any arguments, will display the default message
		if(args.isEmpty()) {
			embed
				.setTitle("| maestro help |")
				.setDescription("use `" + prefix + "help [command]` for more information about a command")
				.addField("basic commands", "`help`, `ping`, `hello`, `blackjack`, `snipe`, `setprefix`", false)
				.addField("music commands", "`join`, `leave`, `play`, `pause`, `skip`, `stop`, `np`, `queue`, `remove`, `repeat`", false)
				.addField("blackjack", "21", false)
				.addField("sniping", "you first", false)
				.setFooter("by mute | https://github.com/mvte");
			
			channel.sendMessageEmbeds(embed.build()).queue();
			return;
		}
		
		// If there were arguments, we'll look for them in our command 
		String search = args.get(0);
		CommandInterface cmd = manager.getCommand(search);
		
		if(cmd == null) {
			channel.sendMessage("this command does not exist").queue();
			return;
		}

		// Create a custom embed for the command
		embed
			.setTitle(cmd.getName())
			.setDescription(cmd.getHelp(prefix));
		
		channel.sendMessageEmbeds(embed.build()).queue();
		
	}

	public String getName() {
		return "help";
	}

	public String getHelp(String prefix) {
		return "bro";
	}

}
