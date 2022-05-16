package maestro.blackjack.interactions;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class StartButton implements Interaction {
	
	public void handle(ButtonInteractionEvent event) {
		GuildGameManager gameManager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		boolean started = gameManager.started;
		TextChannel channel = event.getTextChannel();
		EmbedBuilder eb = new EmbedBuilder();
		
		if(started == true) {
			event.editComponents().queue();
			channel.sendMessage("game has already started").queue();
			return;
		}
		
		started = true;
		eb.setTitle("welcome to blackjack").setDescription(":white_check_mark: game has started");
		event.editComponents().queue();
		channel.sendMessageEmbeds(eb.build()).queue();
		
		gameManager.beginGame(event.getTextChannel(), event.getUser());
	}
	
	@Override
	public String getId() {
		return "blackjack:startbutton";
	}

}
