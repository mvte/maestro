package maestro.blackjack.interactions;

import maestro.Bot;
import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.sql.Connection;
import java.sql.Statement;

public class JoinButton implements Interaction {

	public void handle(ButtonInteractionEvent event) {
		GuildGameManager manager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		TextChannel channel = event.getChannel().asTextChannel();
		event.deferEdit().queue();
		
		if(manager.started) { 
			event.getHook().sendMessage("you cannot join a game that has already started").setEphemeral(true).queue();
			return;
		}

		if(manager.getConnection() == null) {
			event.getHook().sendMessage("something went wrong joining the game, try again").setEphemeral(true).queue();
			manager.retryConnection();
			return;
		}

		Player player = BlackjackManager.getInstance().getPlayerFromDb(event.getUser(), manager.getConnection());
		if(player == null) {
			event.getHook().sendMessage("something went wrong joining the game, try again").setEphemeral(true).queue();
			return;
		}
		
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
