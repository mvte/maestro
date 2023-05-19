package maestro.command.commands;

import java.awt.Color;
import java.util.List;

import maestro.CommandManager;
import maestro.PrefixManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help implements CommandInterface{
	
	private final CommandManager manager;
	
	public Help(CommandManager manager) {
		this.manager = manager;
	}
	
	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getChannel().asTextChannel();
		EmbedBuilder embed = new EmbedBuilder().setColor(new Color(0x000000));
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		// Help command was invoked without any arguments, will display the default message
		if(args.isEmpty()) {
			embed
				.setTitle("| maestro help |")
				.setDescription("use `" + prefix + "help [command]` for more information about a command")
				.addField("basic commands", "`help`, `ping`, `hello`, `blackjack`, `setprefix`, `bugs`, `support`, `purge`, `coinflip`", false)
				.addField("music commands", "`join`, `leave`, `play`, `pause`, `skip`, `stop`, `np`, `queue`, `remove`, `repeat`, `playskip`", false)
				//.addField("math commands", "`approxpi`, `countprimes`", false) 	these commands can break the bot D:
				.addField("blackjack", "21", false)
				//.addField("sniping", "you first", false)
				.setFooter("by mute | https://github.com/mvte")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
			
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
			.setDescription(cmd.getHelp(prefix))
			.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		if(!cmd.getAliases().isEmpty()) {
			String aliases = "`";
			for(String alias : cmd.getAliases()) {
				aliases += alias + "`, `";
			}
			
			aliases = aliases.substring(0, aliases.length()-3);
			embed.addField("aliases", aliases, false);
		}
		
		channel.sendMessageEmbeds(embed.build()).queue();
	}

	public String getName() {
		return "help";
	}

	public String getHelp(String prefix) {
		return "displays the list of commands this bot has";
	}
	
	@Override
	public List<String> getAliases() {
		return List.of("commands");
	}

}
