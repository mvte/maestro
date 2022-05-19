package maestro.blackjack.interactions;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class JoinButton implements Interaction {

	
	public void handle(ButtonInteractionEvent event) {
		GuildGameManager manager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		TextChannel channel = event.getTextChannel();
		event.deferEdit().queue();
		
		if(manager.started) { 
			channel.sendMessage("you cannot join a game that has already started").queue();
			return;
		}
		
		Player player = new Player(1000, event.getUser());
		
		for(Player p : manager.getPlayers()) {
			if(p.getUser().equals(event.getUser())) {
				channel.sendMessage("this player has already joined the game").queue();
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
