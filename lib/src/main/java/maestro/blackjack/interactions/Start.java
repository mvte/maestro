package maestro.blackjack.interactions;

import java.util.List;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.GuildGameManager;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Start implements Interaction {

	public boolean textCommand = true;
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getTextChannel();
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("| starting a game of blackjack |")
		.setDescription("wait for others to join using the join button or play a game by yourself. press start when you're ready, or cancel to cancel.")
		.addField("settings", "starting cash: `1000`\nnumber of decks: `6`\ndealer hits on soft 17", false)
		.setFooter("by mute | https://github.com/mvte")
		.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		GuildGameManager gameManager = BlackjackManager.getInstance().getGameManager(event.getGuild());
		gameManager.nullGame();
		gameManager.addPlayer(new Player(1000, event.getAuthor()));
		
		channel.sendMessageEmbeds(embed.build()).setActionRow(Button.success("blackjack:startbutton", "start"), Button.danger("blackjack:cancelbutton", "cancel"), Button.secondary("blackjack:join_button", "join")).queue();
		return;
	}
	
	@Override
	public String getId() {
		return "blackjack:start";
	}
	
	

}
