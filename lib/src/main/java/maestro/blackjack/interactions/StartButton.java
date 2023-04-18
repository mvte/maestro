package maestro.blackjack.interactions;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class StartButton implements Interaction {
	
	public void handle(ButtonInteractionEvent event) {
		GuildGameManager gameManager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		boolean started = gameManager.started;
		TextChannel channel = event.getChannel().asTextChannel();
		EmbedBuilder eb = new EmbedBuilder();
		
		if(started == true) {
			event.editComponents().queue();
			channel.sendMessage("game has already started").queue();
			return;
		}
		
		if(!gameManager.getPlayers().get(0).getUser().equals(event.getUser())) {
			event.deferEdit().queue();
			event.getHook().sendMessage("you don't have permission to start this game").setEphemeral(true).queue();
			return;
		}
		
		started = true;
		eb.setTitle("welcome to blackjack").setDescription(":white_check_mark: game has started")
			.setFooter("maestro may take a while to respond, please be patient")
			.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		event.editComponents().queue();
		channel.sendMessageEmbeds(eb.build()).queue();
		
		gameManager.beginGame(event.getChannel().asTextChannel());
	}
	
	@Override
	public String getId() {
		return "blackjack:startbutton";
	}

}
