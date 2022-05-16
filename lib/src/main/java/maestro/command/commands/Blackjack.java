package maestro.command.commands;

import java.util.List;

import maestro.blackjack.BlackjackManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Blackjack implements CommandInterface {
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		BlackjackManager.getInstance().handleCommand(event, args);
	}

	@Override
	public String getName() {
		return "blackjack";
	}

	@Override
	public String getHelp(String prefix) {
		return "displays the blackjack menu with all information you need about the game blackjack" +
					"usage: " + prefix + ".blackjack `[command]`\n" +
					"`start` begins a game of blackjack";
	}

}


