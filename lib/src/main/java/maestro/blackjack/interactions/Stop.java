package maestro.blackjack.interactions;

import java.util.List;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Stop implements Interaction {

	public void handle(MessageReceivedEvent event, List<String> args) {
		GuildGameManager gameManager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		
		gameManager.stop(event.getTextChannel());
	}
	
	@Override
	public String getId() {
		return "blackjack:stop";
	}

	
	
}
