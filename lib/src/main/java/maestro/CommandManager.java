package maestro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import maestro.command.CommandInterface;
import maestro.command.admin.Purge;
import maestro.command.admin.SetPrefix;
import maestro.command.commands.Blackjack;
import maestro.command.commands.Bugs;
import maestro.command.commands.Coinflip;
import maestro.command.commands.Hello;
import maestro.command.commands.Help;
import maestro.command.commands.Micaela;
import maestro.command.commands.Ping;
import maestro.command.commands.Support;
import maestro.command.commands.math.CountPrimes;
import maestro.command.commands.music.Join;
import maestro.command.commands.music.Leave;
import maestro.command.commands.music.NowPlaying;
import maestro.command.commands.music.Pause;
import maestro.command.commands.music.Play;
import maestro.command.commands.music.Playskip;
import maestro.command.commands.music.Queue;
import maestro.command.commands.music.Remove;
import maestro.command.commands.music.Repeat;
import maestro.command.commands.music.Skip;
import maestro.command.commands.music.Stop;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

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
		//addCommand(new Snipe());
		addCommand(new Join());
		addCommand(new Leave());
		addCommand(new Play());
		addCommand(new Stop());
		addCommand(new Skip());
		addCommand(new NowPlaying());
		addCommand(new Queue());
		addCommand(new Repeat());
		addCommand(new Remove());
		addCommand(new Pause());
		addCommand(new SetPrefix());
		//addCommand(new ApproximatePi());
		addCommand(new Bugs());
		addCommand(new CountPrimes());
		addCommand(new Playskip());
		addCommand(new Support());
		addCommand(new Purge());
		addCommand(new Coinflip());
	}

	private void addCommand(CommandInterface cmd) {
		// If the command already exists, then we don't want to add it to the command list.
		boolean exists = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

		if (exists)
			throw new IllegalArgumentException("this command already exists");

		commands.add(cmd);
	}

	public List<CommandInterface> getCommands() {
		return commands;
	}

	@Nullable
	public CommandInterface getCommand(String search) {
		search = search.toLowerCase();

		for (CommandInterface cmd : this.commands) {
			if (cmd.getName().equals(search) || cmd.getAliases().contains(search))
				return cmd;
		}

		return null;
	}

	void handle(MessageReceivedEvent event, String prefix) {
		// Splits the command into its constituent parts, without the command prefix
		// (the command is the first argument)
		String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "")
				.split("\\s+");

		// The command being invoked
		String invoke = split[0].toLowerCase();
		CommandInterface cmd = this.getCommand(invoke);

		if (cmd != null) {
			List<String> args = Arrays.asList(split).subList(1, split.length);

			cmd.handle(event, args);
		}

	}

}
