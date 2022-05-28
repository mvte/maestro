package maestro.blackjack.interactions;

import java.util.List;

import maestro.PrefixManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Display implements Interaction {

	public boolean textCommand = true;
	
	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		EmbedBuilder embed = new EmbedBuilder();
		TextChannel channel = event.getTextChannel();
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		embed.setTitle("| blackjack |")
		.setDescription("welcome to blackjack! use `" + prefix + "blackjack start` to begin")
		.addField("description" ,"your hand must beat the dealer's hand without going over 21. you and the dealer will both be dealt two cards, after which you will be dealt one card at a time until you \"stand\". " + 
				"the dealer will deal himself cards until they wish to stop. whoever's hand has the highest value wins.", false)
		.addField("rules", "use `" + prefix + "blackjack rules`", false)
		.addField("commands", "`start` begins a game of blackjack\n" /*+ 
				"`stop` stops an ongoing game of blackjack (you must have admin privileges). if you created a game and you wish to stop it, you must do so after a round is finished\n"*/, false)
		.setFooter("by mute | https://github.com/mvte")
		.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		channel.sendMessageEmbeds(embed.build()).queue();
	}
	
	@Override
	public String getId() {
		return "blackjack:display";
	}

}
