package maestro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import maestro.command.CommandInterface;
import maestro.command.commands.*;
import maestro.command.commands.music.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * CommandManager
 * 
 * 
 */
public class CommandManager {

	private final List<CommandInterface> commands = new ArrayList<>();

	public CommandManager() {
		addCommand(new Ping());
		addCommand(new Help(this));
		addCommand(new Hello());
		addCommand(new Micaela());
		addCommand(new Blackjack());
		addCommand(new Snipe());
		addCommand(new Join());
		addCommand(new Leave());
		addCommand(new Play());
		addCommand(new Stop());
		addCommand(new Skip());
	}

	private void addCommand(CommandInterface cmd) {
		// If the command already exists, then we don't want to add it to the command
		// list.
		boolean exists = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

		if (exists)
			throw new IllegalArgumentException("This command already exists");

		commands.add(cmd);
	}

	public List<CommandInterface> getCommands() {
		return commands;
	}

	@Nullable
	public CommandInterface getCommand(String search) {
		search = search.toLowerCase();

		for (CommandInterface cmd : this.commands) {
			if (cmd.getName().equals(search))
				return cmd;
		}

		return null;
	}

	void handle(MessageReceivedEvent event) {
		// Splits the command into its constituent parts, without the command prefix
		// (the command is the first argument)
		String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(Bot.prefix), "")
				.split("\\s+");

		// The command being invoked
		String invoke = split[0].toLowerCase();
		CommandInterface cmd = this.getCommand(invoke);

		if (cmd != null) {
			event.getChannel().sendTyping().queue();
			List<String> args = Arrays.asList(split).subList(1, split.length);

			cmd.handle(event, args);
		}

	}

}
