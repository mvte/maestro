package maestro.blackjack.interactions;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Rules implements Interaction {

	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getTextChannel();
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.setTitle("blackjack rules")
			.setDescription("all casinos may have some variations on the version of blackjack that they play. this guide will go over how maestro's blackjack is played")
			.setThumbnail(channel.getGuild().getSelfMember().getEffectiveAvatarUrl())
			.addField("basics", "the main premise of blackjack is to beat the dealer's hand without going over 21. before any turn begins, all players will set their bets, then the dealer will deal everyone two cards each, including dealer themself. " +
						"during your turn, you have the option of hitting, standing, or doubling down. if you choose to hit, the dealer will deal you another card. you may hit as many times as you would like " +
						"during your turn, but beware not to go over 21, or you \"bust\". you may choose to stand at any point during your turn (given you haven't busted) which ends your turn. if " +
						"you choose to double down, your wager will be doubled in exchange for one extra card, after which your turn will end. you may only double down at the beginning of your turn. once everyone " +
						"has finished playing, the dealer will deal himself cards, being subject to the same limitations as you: not going over 21. the dealer will hit on a soft 17.", false)
			.addField("betting", "before any cards are dealt, each player must set a bet. if you beat the dealer without going over 21, you will be paid 2:1. if you beat the dealer with blackjack, you " +
						"you will be paid 3:2. if the dealer beats you, or you have busted, your bet is forfeit. if you tie with the dealer, you \"push\", and you will receive your bet back", false)
			.addField("dealing", "the dealer will deal each player two cards, including himself. the dealer's second card will be face down. if the dealer's face up card is 10, the dealer will check the face down card "
					+ "before continuing play. if the dealer gets blackjack, the round ends, and all players who haven't also gotten blackjack will automatically lose.", false)
			.addField("cards", "this version of blackjack is played with a 6-deck shoe, each deck being a 52-card jokerless deck. every card 2-10 simply has the value of its rank. " +
						"meanwhile, the king, queen, and jack are all valued at 10, and the ace is valued at 1 or 11, whichever is most advantageous to the hand. when a hand's ace is valued at 11, it is considered to be \"soft\".", false)
			.addField("split hands", "when a player's first two cards have the same rank (e.g. a king of spades and a king of diamonds), the player may choose to split their hand into two. each hand will have the same bet as the " +
						"original, and the dealer will treat each hand as a separate player. note, a player may only split their hand if they have the money to do so.", true)
			.addField("insurance", "when the dealer's face up card is an ace, the players may choose to bet insurance. in the event that the dealer gets dealt a blackjack, this bet would mitigate their loss. a player may only bet insurance " + 
						" up to half of their original bet.", false)
			.addField("seven card charlie", "if a player manages to get dealt 7 cards without going over 21, they automatically win the round, regardless of whether or not they beat the dealer.", true)
			.setFooter("by mute | https://github.com/mvte");
			
		
		channel.sendMessageEmbeds(eb.build()).queue();
		
	}
	
	@Override
	public String getId() {
		return "blackjack:rules";
	}
	
	

}
