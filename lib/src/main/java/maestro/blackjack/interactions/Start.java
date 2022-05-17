package maestro.blackjack.interactions;

import java.util.List;

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
		.setDescription("press the start button to start a game of blackjack, or press cancel to cancel")
		.addField("settings", "a normal game of blackjack\n(custom settings coming soon)", false)
		.setFooter("by mute | https://github.com/mvte");
		
		channel.sendMessageEmbeds(embed.build()).setActionRow(Button.success("blackjack:startbutton", "start"), Button.danger("blackjack:cancelbutton", "cancel")).queue();
		return;
	}
	
	@Override
	public String getId() {
		return "blackjack:start";
	}
	
	

}