package maestro.blackjack.interactions;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import maestro.Bot;
import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Start implements Interaction {

	public boolean textCommand = true;
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getChannel().asTextChannel();
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("| starting a game of blackjack |")
		.setDescription("wait for others to join using the join button or play a game by yourself. press start when you're ready, or cancel to cancel.")
		.addField("settings", "starting cash: `1000`\nnumber of decks: `6`\ndealer hits on soft 17", false)
		.setFooter("by mute | https://github.com/mvte")
		.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		GuildGameManager gameManager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		if(gameManager.started) {
			channel.sendMessage("a game has already been started").queue();
			return;
		}

		if(gameManager.getConnection() == null) {
			channel.sendMessage("something went wrong starting the game, try again").queue();
			gameManager.retryConnection();
			return;
		}
		Player player = BlackjackManager.getInstance().getPlayerFromDb(event.getAuthor(), gameManager.getConnection());
		if(player == null) {
			channel.sendMessage("something went wrong starting the game, try again").queue();
			return;
		}
		gameManager.addPlayer(player);

		channel.sendMessageEmbeds(embed.build()).setActionRow(Button.success("blackjack:startbutton", "start"), Button.danger("blackjack:cancelbutton", "cancel"), Button.secondary("blackjack:join_button", "join")).queue();
	}
	
	@Override
	public String getId() {
		return "blackjack:start";
	}
	
	

}
