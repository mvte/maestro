package maestro.command.commands;

import java.util.List;

import maestro.Bot;
import maestro.PrefixManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Blackjack implements CommandInterface {
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		EmbedBuilder embed = new EmbedBuilder()
				.setTitle("| blackjack |")
				.setDescription("welcome to blackjack! use `" + prefix + "blackjack start` to begin")
				.addField("description" ,"your hand must beat the dealer's hand without going over 21. you and the dealer will both be dealt two cards, after which you will be dealt one card at a time until you \"stand\". " + 
						"the dealer will deal himself cards until they wish to stop. whoever's hand has the highest value wins ;)", false)
				.addField("rules", "use google", false)
				.addField("commands", "`start` begins a game of blackjack\n" + 
						"`stop` stops an ongoing game of blackjack (you must have admin privileges). if you created a game and you wish to stop it, you must do so after a round is finished\n", false);
		
		channel.sendMessageEmbeds(embed.build()).queue();
	}

	@Override
	public String getName() {
		return "blackjack";
	}

	@Override
	public String getHelp(String prefix) {
		return "displays the blackjack menu with all information you need about the game blackjack" +
					"usage: " + prefix + ".blackjack `[command]`\n" +
					"`start` begins a game of blackjack";
	}

}


