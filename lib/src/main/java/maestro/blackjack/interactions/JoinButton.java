package maestro.blackjack.interactions;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class JoinButton implements Interaction {

	public void handle(ButtonInteractionEvent event) {
		GuildGameManager manager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		TextChannel channel = event.getChannel().asTextChannel();
		event.deferEdit().queue();
		
		if(manager.started) { 
			event.getHook().sendMessage("you cannot join a game that has already started").setEphemeral(true).queue();
			return;
		}
		
		Player player = new Player(1000, event.getUser());
		
		for(Player p : manager.getPlayers()) {
			if(p.getUser().equals(event.getUser())) {
				event.getHook().sendMessage("you have already joined the game").setEphemeral(true).queue();
				return;
			}
		}
		
		channel.sendMessage(channel.getGuild().getMember(player.getUser()).getAsMention() + " has joined the game").queue();
		manager.addPlayer(player);
	}
	
	@Override
	public String getId() {
		return "blackjack:join_button";
	}

}
