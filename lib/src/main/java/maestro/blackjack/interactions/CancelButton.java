package maestro.blackjack.interactions;

import maestro.PrefixManager;
import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class CancelButton implements Interaction {

	public void handle(ButtonInteractionEvent event) {
		GuildGameManager gameManager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		TextChannel channel = event.getChannel().asTextChannel();
		boolean started = gameManager.started;
		EmbedBuilder eb = new EmbedBuilder();
		
		if(started == true) {
			event.editComponents().queue();
			channel.sendMessage("you cannot cancel an in progress game").queue();
			return;
		}
		gameManager.nullGame();
		
		eb.setTitle(":x: game canceled!").setDescription("use `" + prefix + "blackjack start` to start a game");
		event.editComponents().queue();
		channel.sendMessageEmbeds(eb.build()).queue();
		return;
	}
	
	@Override
	public String getId() {
		return "blackjack:cancelbutton";
	}

}
