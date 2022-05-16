package maestro.blackjack;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import maestro.Bot;
import maestro.blackjack.objects.Card;
import maestro.blackjack.objects.Deck;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Game {
	
	/**
	 * TODO
	 * - double down (+ logic)
	 * 		- player should only be able to double down if they have enough money
	 * 		- you probably have to figure out actionrows
	 * 		- double down button should be disabled if player cannot double down
	 * - insurance bets
	 * 		- when dealer gets an ace, players should be allowed to bet insurance
	 * - 7 card charlie
	 * 		- when a player manages to get dealt 7 cards, they are compensated with an award
	 * - split hand
	 * 		- when a player is dealt two cards of the same face, they should be allowed to split hands (if they choose)
	 * - fix the ui
	 * 		- maybe adding a long -------------------- will help
	 * 		- space it in between certain messages
	 * - tell the user that he must click the hit button again if he wishes to continue hitting
	 * - minimize lag, or add a typing queue where it happens
	 * - replace getEffectiveName() with getEffectiveName() (just in case the user has no nickname)
	 * 
	 * memory
	 * 	there is a memory issue for games that run for extremely long times, as the game is built by calling methods within methods
	 * 	if these methods don't return, the memory will never be reclaimed from the methods that were called previously
	 * 	idk how to fix this lmao
	 * 		(design the game better?)
	 */
	
	private Deck deck;
	private Player player;
	private Player dealer;
	private TextChannel channel;
	private EventWaiter waiter = Bot.waiter;
	public boolean started = false;
	
	public Game(int numDecks, TextChannel channel, User user) {
		channel.getGuild().retrieveMemberById(user.getId()).queue();
		deck = new Deck(true, numDecks);
		player = new Player(1000, user);
		this.channel = channel;
	}
	
	//treat this like the actual game
	public void run(boolean first) {
		dealer = new Player();
		
		if(!first) {
			player.resetHand();
		}
		
		//because of the way eventwaiter works, we need to have everything linked starting from this method
		//	i have no idea how to keep the game going after one round lmao
		setBet(player);
	
		
	}
	
//	void initialize(boolean first) {
//		dealer = new Player();
//		
//		if(!first) {
//			player.resetHand();
//		}
//		
//		setBets();
//		dealToPlayers();
//		
//	}
	
	/**
	 * Takes input from a player to 
	 * @param player The player we're accepting 
	 */
	private void setBet(Player player) {
		//you should recursively call this method for every player in the game if you plan to add multiplayer
		//	the players should be kept in a linked list
		//something like
		//	if(playerNode.next == null) nextMethod();
		//  else setBet(playerNode.next());
		
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("current balances")
				.addField(player.getName(), String.format("$%.2f", player.getCash()), false);
		channel.sendMessageEmbeds(eb.build()).queue();
		
		channel.sendMessage(player.getUser().getAsMention() + ", please enter your bet").queue();
		waiter.waitForEvent(MessageReceivedEvent.class, 
			(e) -> {
				if(e.getAuthor().isBot() || !e.getAuthor().equals(player.getUser())) {
					return false;
				}
				
				try {
					double i = Double.parseDouble(e.getMessage().getContentRaw());
					if(i <= 0) {
						channel.sendMessage("bet must be positive").queue();
						return false;
					}
					
					if(i > player.getCash()) {
						channel.sendMessage("you don't have enough money broke ass").queue();
						return false;
					}
					
				} catch (Exception x){
					channel.sendMessage("please enter a number").queue();
					return false;
				}
				
				return true;
			}, 
			
			(e) -> {
				double i = Double.parseDouble(e.getMessage().getContentRaw());
				player.setWager(i);
				dealToPlayers(player);
			}, 30, TimeUnit.SECONDS, () -> channel.sendMessage("you took too long (betting)").queue());
		
	}
	
	private void dealToPlayers(Player player) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("dealing")
				.setDescription("maestro is dealing cards, please wait");
		channel.sendMessageEmbeds(eb.build()).queue();
		
		//a card is dealt to every player once (including the dealer) before dealing the second card to all players
		for(int i = 0; i < 2; i++) {
			Card dealingCard = deck.deal();
			
			//deal to player (for multiple players, you should loop)
			channel.sendMessage("dealing to " + player.getUser().getAsMention() + ": " + dealingCard.toString()).queue();
			player.getHand().add(dealingCard);
			
			//deal to dealer
			dealingCard = deck.deal();
			if(i==0)
				channel.sendMessage("dealing to DEALER: " + dealingCard.toString()).queue();
			else
				channel.sendMessage("dealing to DEALER: FACE DOWN").queue();
			dealer.getHand().add(dealingCard);
		}		
		
		
		turn(player);
	}
	
	private void turn(Player p) {
		//again, same deal as setBets: recursively call this function for each player
		//	when a player stands, call turn(playerNode.next()) if the next player is not null, otherwise go to the next method
		//channel.getGuild().retrieveMemberById(p.getUser().getId()).queue();
		
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle(channel.getGuild().getMember(p.getUser()).getEffectiveName() + "'s turn")
			.addField("current hand", p.getHand().toString(), false)
			.addField("hand value", p.getHand().value() + "", false);
		
		channel.sendMessageEmbeds(eb.build()).setActionRow(Button.primary("blackjack:hitbutton", "hit"), Button.secondary("blackjack:standbutton", "stand")).queue( m ->
		waiter.waitForEvent(ButtonInteractionEvent.class, 
			(e) -> {
				return e.getUser().equals(player.getUser()) && e.getMessageIdLong() == m.getIdLong();
			},
			(e) -> {
				e.deferEdit().queue();
				play(p, e);
				
				//call next method or call this method again (for a different player)
				
			}, 30, TimeUnit.SECONDS, () -> channel.sendMessage("you took too long (hitting)").queue()));
			
	}
	
	
	private void play(Player player, ButtonInteractionEvent event) {
		//recursively call play until the player busts or stands
		if(event.getComponentId().equals("blackjack:standbutton")) {
			channel.sendMessage(player.getUser().getAsMention() + " stands").queue();
			dealerTurn(player);
			return;
		}
		
		if(event.getComponentId().equals("blackjack:hitbutton")) {
			Card dealingCard = deck.deal();
			player.getHand().add(dealingCard);
			channel.sendMessage("dealing to " + player.getUser().getAsMention() + ": " + dealingCard.toString()).queue();
			channel.sendMessage("hand value: " + player.getHand().value()).queue();
			
			if(player.getHand().value() > 21) {
				channel.sendMessage(player.getUser().getAsMention() + " busts").queue();
				//if playerNode.next != null
				//	call turn(p) for that player, otherwise it is the dealer's turn
				dealerTurn(player);
				return;
			}
			
			if(player.getHand().value() == 21) {
				channel.sendMessage(player.getUser().getAsMention() + " has blackjack!").queue();
				//if playerNode.next != null
				//	call turn(p) for that player, otherwise it is the dealer's turn
				dealerTurn(player);
				return;
			}
			
			//wait for another hit or stand (then call this method again)
			waiter.waitForEvent(ButtonInteractionEvent.class, 
				e -> {
					return e.getUser().equals(player.getUser()) && e.getMessageIdLong() == event.getMessageIdLong();
				}, 
				e -> { 
					play(player, e);
				}, 30, TimeUnit.SECONDS, () -> channel.sendMessage("you took too long (playing)").queue());
			
		}
	}
	
	private void dealerTurn(Player player) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("dealer's turn")
				.setDescription("please wait");
		
		channel.sendMessageEmbeds(eb.build()).queue();
		channel.sendMessage("dealer's down card is... " + dealer.getHand().get(1) + "\nhand value: " + dealer.getHand().value()).queue();
		
		//the dealer deals until their hand is greater than 17
		Card dealingCard;
		while(dealer.getHand().value() < 17) {
			dealingCard = deck.deal();
			dealer.addToHand(dealingCard);
			channel.sendMessage("dealing to DEALER: " + dealingCard + "\nhand value: " + dealer.getHand().value()).queue();
		}
		
		//displays the result of the dealer's turn
		if(dealer.getHand().value() > 21) {
			channel.sendMessage("dealer busts!").queue();
		}else if(dealer.hasBlackjack()) {
			channel.sendMessage("dealer has blackjack!").queue();
		}else {
			 channel.sendMessage("dealer stands!").queue();
		}
		
		payout(player);
	}
	
	private void payout(Player player) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("payout");
		
		eb.addField(channel.getGuild().getMember(player.getUser()).getEffectiveName(), evaluatePay(player), false);
		channel.sendMessageEmbeds(eb.build()).queue();
		
		endGame();
	}
	
	/** 
	 * Evaluates the pay for a certain player.
	 * @param p the player whose pay is being evaluated
	 * postcondition:
	 * if the player's hand is more than 21, they bust and the bet is forfeit
	 * if the dealer's hand is more than 21, all remaining players win (they get their bet back and win an additional amount equals to their bet)
	 * if the player's hand is equal to the dealer's hand, the player pushes (wins nothing, loses nothing)
	 * if the player's hand is equal to 21,  the player wins with an increased payout (x1.5)
	 * if the player's hand is greater than the dealer's hand, the player wins
	 * if the player's hand is less than the dealer's,  their bet is forfeit
	 * if the player has bet insurance, their bet is paid back if the dealer's down card value is 10
	 * 
	 * these if statements are tested in this order, such that some possibilities are ruled out as it progresses
	 * e.g. this cancels situations like: 
	 * paying out to player because the player has a higher hand value, but the player has busted.
	 */
	private String evaluatePay(Player p) {
		String result = "";
		
		if(p.getHand().value() > 21) {
			result = p.getName() + " has busted. (-$" + p.getWager() + ")";
		}else if(dealer.getHand().value() > 21) {
			if(!p.hasBlackjack()) {
				result = "dealer has busted. " + p.getName() + " has won. (+$" + p.getWager() + ")";
				p.addCash(p.getWager()*2);
			}else {
				result = "dealer has busted. " + p.getName() + " has won with blackjack. (+$" + p.getWager()*1.5 + ")";
				p.addCash(p.getWager()*2.5);
			}
		}else if(p.getHand().value() == dealer.getHand().value()) {
			result = p.getName() + " pushes. (+$0)";
			p.addCash(p.getWager());
		}else if(p.hasBlackjack()) {
			result = p.getName() + " has won with blackjack. (+$" + (p.getWager()*1.5) + ")";
			p.addCash(p.getWager()*2.5);
		}else if(p.getHand().value() > dealer.getHand().value() || p.getHand().size() == 7) {
			result = p.getName() + " has won. (+$" + p.getWager() + ")";
			p.addCash(p.getWager()*2);
		}else if(p.getHand().value() < dealer.getHand().value()) {
			result = p.getName() + " has lost. (-$" + p.getWager() + ")";
		}
		
		return result;
	}
	
	private void endGame() {
		EmbedBuilder eb = new EmbedBuilder();
		
		if(player.getCash() == 0) {
			channel.sendMessage("you have no money left, ending game...").queue();
			stop();
		}
			eb.setTitle("round has ended")
				.setDescription("do you wish to continue?");
		
		channel.sendMessageEmbeds(eb.build()).setActionRow(Button.success("blackjack:continuebutton", "continue"), Button.danger("blackjack:end_game_button", "end game")).queue(msg ->
		waiter.waitForEvent(ButtonInteractionEvent.class, e -> {
			return e.getUser().equals(player.getUser()) && e.getMessageIdLong() == msg.getIdLong();
		},
		e -> {
			if(e.getComponentId().equals("blackjack:continuebutton")) {
				e.editComponents().queue();
				channel.sendMessage("continuing game").queue();
				run(false);
				return;
			}
			
			if(e.getComponentId().equals("blackjack:end_game_button")) {
				e.editComponents().queue();
				stop();
				return;
			}
			
		}, 30, TimeUnit.SECONDS, () -> {
			channel.sendMessage("received no response, stopping game").queue();
			stop();
		}));
	}
	
	public void stop() {
		started = false;
		
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("thanks for playing blackjack <3")
				.setDescription("here's how you did")
				.setFooter("by mute | https://github.com/mvte");
		
		//loop for multiple players
		eb.addField(channel.getGuild().getMember(player.getUser()).getEffectiveName(), String.format("$%.2f", player.getCash()), false);
			//should also save to database here (if you do add it)
		
		channel.sendMessageEmbeds(eb.build()).queue();
	}
	
}
