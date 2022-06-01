package maestro.blackjack.interactions;

import maestro.blackjack.BlackjackManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class Leave implements Interaction {

	@Override
	public void handle(ButtonInteractionEvent event) {
		Guild guild = event.getGuild();
		
		BlackjackManager.getInstance().getGameManager(guild).removePlayer(event.getUser(), event);
	}
	
	@Override
	public String getId() {
		return "blackjack:leave_game_button";
	}

}
