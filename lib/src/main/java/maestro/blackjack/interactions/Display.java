package maestro.blackjack.interactions;

import java.util.List;

import maestro.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Display implements Interaction {

	public boolean textCommand = true;
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		EmbedBuilder embed = new EmbedBuilder();
		TextChannel channel = event.getChannel().asTextChannel();
		String prefix = Config.get("prefix");
		
		embed.setTitle("| blackjack |")
		.setDescription("welcome to blackjack! use `" + prefix + "blackjack start` to begin")
		.addField("description" ,"your hand must beat the dealer's hand without going over 21. you and the dealer will both be dealt two cards, after which you will be dealt one card at a time until you \"stand\". " + 
				"the dealer will deal himself cards until they wish to stop. whoever's hand has the highest value wins.", false)
		.addField("rules", "use `" + prefix + "blackjack rules`", false)
		.addField("commands", "`start` begins a game of blackjack\n" +
				"`leaderboard` displays ranking of players by cash amount\n" +
				"`cash` displays how much cash you have", false)
		.setFooter("by mute | https://github.com/mvte")
		.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		channel.sendMessageEmbeds(embed.build()).queue();
	}
	
	@Override
	public String getId() {
		return "blackjack:display";
	}

}
