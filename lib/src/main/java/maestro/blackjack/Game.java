package maestro.blackjack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import maestro.Bot;
import maestro.blackjack.objects.Card;
import maestro.blackjack.objects.Deck;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Game {
	
	/**
	 * TODO
	 * - multiplayer!! 
	 * 		- revelation: the game is built like a circle, where each method calls another and another until it calls the first method again restarting the loop
	 * 			- therefore, if we deviate from the circle, we must return to the circle to keep the game going
	 * 				- this logic can be applied to the split hands
	 * - double down (+ logic) (DONE)
	 * 		- player should only be able to double down if they have enough money
	 * 		- you probably have to figure out actionrows
	 * 		- double down button should be disabled if player cannot double down
	 * - insurance bets (DONE)
	 * 		- when dealer gets an ace, players should be allowed to bet insurance
	 * - 7 card charlie (DONE)
	 * 		- when a player manages to get dealt 7 cards, they are compensated with an award
	 * - split hand (DONE)
	 * 		- when a player is dealt two cards of the same face, they should be allowed to split hands (if they choose)
	 * - fix the ui (done?)
	 * 		- maybe adding a long ─────────────── will help
	 * 		- space it in between certain messages
	 * 			- these should be placed at the end of every player's turn (bust, stand, blackjack, etc.)
	 * 		- add thumbnails so it's not so empty
	 * - tell the user that he must click the hit button again if he wishes to continue hitting (DONE)
	 * - minimize lag, or add a typing queue where it happens
	 * - replace getNickname() with getEffectiveName() (just in case the user has no nickname) (DONE)
	 * - fix splithand and insurance bet compatibility
	 * 		- separate the split situation into it's own method, and call it in both turn() and insuranceSituation()
	 * - game doesn't stop properly when stop command is called
	 * 
	 * NOTES
	 * memory
	 * 	there may be a memory issue for games that run for extremely long times, as the game is built by calling methods within methods
	 * 	if these methods don't return, the memory will never be reclaimed from the methods that were called previously
	 * 	idk how to fix this lmao
	 * 		design the game better? (we're far too deep for this)
	 * 		i think returning after calling a method with a lambda function is good enough (we are async anyways)
	 * 
	 * circle
	 * 		 |------------------------------------------<---------------------------------------------------|
	 * 		run -> setbet -> dealToPlayers -> turn -> play -> dealerTurn -> payout (calls evaluatePay) -> endGame -> stop
	 * 	                      |------------------------------->------|
	 *										   |---------|---->------|
	 *										   |--split--|---->------|
	 *
	 * multiplayer
	 * 		for split hands, create a new playerNode which takes the player's split hand, then set the next node the split player's next node
	 * 			similarly, you can also insert the split node after the current player node
	 * 				but you've already implemented split hands using a convoluted system of booleans so... if it ain't broke don't fix it	
	 * 					(actually, this might solve your insurance system compatibility)
	 *
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
		dealer = new Player();
	}
	
	//treat this like the actual game
	public void run(boolean first) {
		dealer.resetHand();
		
		if(!first) {
			player.resetHand();
		}
		
		//because of the way eventwaiter works, we need to have everything linked starting from this method
		setBet(player);
	
		
	}
	
	/**
	 * Takes input from a player to determine their bet
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
				.addField(channel.getGuild().getMember(player.getUser()).getEffectiveName(), String.format("$%.2f", player.getCash()), false)
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
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
			}, 30, TimeUnit.SECONDS, () -> {
				channel.sendMessage("you took too long betting, default bet set (1)").queue();
				player.setWager(1);
				//send to next method
				dealToPlayers(player);
			});
		
	}
	
	private void dealToPlayers(Player player) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("dealing")
				.setDescription("maestro is dealing cards, please wait")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		channel.sendMessageEmbeds(eb.build()).queue();
		
		//a card is dealt to every player once (including the dealer) before dealing the second card to all players
		for(int i = 0; i < 2; i++) {
			Card dealingCard = deck.deal();
			
			//deal to player (for multiple players, you should loop)
			channel.sendMessage("dealing to " + player.getUser().getAsMention() + ": " + dealingCard.toString()).queue();
			player.getHand().add(dealingCard);
			
			//deal to dealer
			dealingCard = deck.deal();
			if(i==0) {
				channel.sendMessage("dealing to DEALER: " + dealingCard.toString()).queue();
			} else {
				channel.sendMessage("dealing to DEALER: *FACE DOWN*").queue();
			}
			dealer.getHand().add(dealingCard);
		}		
		
		if(dealer.getHand().get(0).getValue() == 1) {
			//this should call itself until all players cycled through
			boolean eligible = false;
			
			//iterate through player list to determine eligibility
			eligible = player.getCash() > player.getWager()*0.5;
			
			if(eligible)
				channel.sendMessage("the dealer has been dealt an ace, some players are eligible for insurance").queue();
			
			insuranceSituation(player);
			return;
		}
		
		//if dealer has blackjack, than none of the players should be allowed to play
		if(dealer.getHand().get(0).getValue() == 10) {
			channel.sendMessage("dealer is checking face down card...").queue();
		}
		
		if(dealer.hasBlackjack()) { 
			channel.sendMessage(dealer.getHand().get(1).toString()).queue();
			channel.sendMessage("dealer has blackjack!").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			payout(player);     
			return;
		}
		
		channel.sendMessage("──────────────────────────────────────").queue();
		
		//ask here what the player would like to do if they want to split hands
		//	iterate through list for multiple players
		if(player.canSplit()) {
			EmbedBuilder ebSpl = new EmbedBuilder()
					.setTitle("split hand")
					.setDescription(channel.getGuild().getMember(player.getUser()).getEffectiveName() + ", you are able to split your hand. this split hand will have the same bet as your original bet. would you like to?")
					.setThumbnail(player.getUser().getAvatarUrl());
			
			channel.sendMessageEmbeds(ebSpl.build()).setActionRow(Button.success("blackjack:yes_split_button", "split"), Button.danger("blackjack:no_split_button", "no")).queue( m ->
			waiter.waitForEvent(ButtonInteractionEvent.class, 
				(e) -> {
					return e.getUser().equals(player.getUser()) && m.getIdLong() == e.getMessageIdLong();
				}, e -> {
					e.editComponents().queue();
					
					if(e.getComponentId().equals("blackjack:yes_split_button")) {
						player.splitPlayerHand();
						channel.sendMessage(player.getUser().getAsMention() + " splits their hand\nsplit hand: " + player.getSplitHand().getHand().toString()).queue();
						turn(player, true);
						return;
					}
					
					if(e.getComponentId().equals("blackjack:no_split_button")) {
						turn(player, false);
						return;
					}
				}, 30, TimeUnit.SECONDS, () -> {
					channel.sendMessage("you took too long, will not split hand").queue();
					turn(player, false);
					return;
				}));
		} else {
			turn(player, false);
		}
		
		
	}
	
	private void turn(Player p, boolean split) {
		//again, same deal as setBets: recursively call this function for each player
		//	when a player stands, call turn(playerNode.next()) if the next player is not null, otherwise go to the next method
		//channel.getGuild().retrieveMemberById(p.getUser().getId()).queue();
		
		String effName = p.isSplitHand() ? String.format("%s's split hand", channel.getGuild().getMember(p.getUser()).getEffectiveName()) : channel.getGuild().getMember(p.getUser()).getEffectiveName();
		
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle(effName + "'s turn")
			.setThumbnail(p.getUser().getAvatarUrl())
			.addField("current hand", p.getHand().toString(), false)
			.addField("hand value", p.getHand().value() + "", false)
			.setFooter("use the buttons below to indicate your decision");
		
		
		//if the player is dealt a blackjack immediately, no need to prompt for hit or stand
		if(p.getHand().value() == 21) {
			channel.sendMessageEmbeds(eb.build()).queue();
			channel.sendMessage(p.getUser().getAsMention() + " has blackjack!").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			
			if(p.isSplitHand()) {
				turn(p.getSplitHand(), false);
			} else {
				dealerTurn(p);
			}
			
			return;
		}
		
		ActionRow ar;
		if(p.getWager() <= p.getCash()) {
			ar = ActionRow.of(Button.primary("blackjack:hitbutton", "hit"), Button.secondary("blackjack:standbutton", "stand"), Button.secondary("blackjack:doubledownbutton", "double down"));
		} else {
			ar = ActionRow.of(Button.primary("blackjack:hitbutton", "hit"), Button.secondary("blackjack:standbutton", "stand"));
		}

		channel.sendMessageEmbeds(eb.build()).setActionRows(ar)
			.queue( m -> 
				waiter.waitForEvent(ButtonInteractionEvent.class, 
					(e) -> {
						return e.getUser().equals(player.getUser()) && e.getMessageIdLong() == m.getIdLong();
					},
					(e) -> {
						play(p, e, split);
						
						//call next method or call this method again (for a different player)
						
					}, 30, TimeUnit.SECONDS, () -> {
						channel.sendMessage("due to inactivity, " + player.getUser().getAsMention() + " stands").queue();
						//send to next method (dealer turn, player turn, or player's split hand turn)
						dealerTurn(p);
					}));
			
	}
	
	
	private void play(Player player, ButtonInteractionEvent event, boolean split) {
		//recursively call play until the player busts or stands
		if(event.getComponentId().equals("blackjack:standbutton")) {
			event.editComponents().queue();
			channel.sendMessage(player.getUser().getAsMention() + " stands").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			
			if(split) {
				turn(player.getSplitHand(), false);
			} else {
				//or next player
				dealerTurn(player);
			}
			
			return;
		}
		
		if(event.getComponentId().equals("blackjack:doubledownbutton")) {
			event.editComponents().queue();
			
			channel.sendMessage(player.getUser().getAsMention() + " doubles down").queue();
			player.doubleWager();
			
			Card dealingCard = deck.deal();
			player.addToHand(dealingCard);
			channel.sendMessage("dealing to " + player.getUser().getAsMention() + ": " + dealingCard + "\nhand value: " + player.getHand().value()).queue();
			
			if(player.getHand().value() > 21 )
				channel.sendMessage(player.getUser().getAsMention() + " busts").queue();
			
			if(player.getHand().value() == 21)
				channel.sendMessage(player.getUser().getAsMention() + " has blackjack!").queue();
			
			channel.sendMessage("──────────────────────────────────────").queue();
			
			if(split) {
				turn(player.getSplitHand(), false);
			} else {
				//or next player
				dealerTurn(player);
			}
			
			return;
			
		}
		
		if(event.getComponentId().equals("blackjack:hitbutton")) {
			
			Card dealingCard = deck.deal();
			player.getHand().add(dealingCard);
			channel.sendMessage("dealing to " + player.getUser().getAsMention() + ": " + dealingCard + "\nhand value: " + player.getHand().value()).queue();
			
			if(player.getHand().size() == 7 && player.getHand().value() <= 21) {
				event.editComponents().queue();
				channel.sendMessage(player.getUser().getAsMention() + " has a 7 card charlie!").queue();
				channel.sendMessage("──────────────────────────────────────").queue();
				
				if(split) {
					turn(player.getSplitHand(), false);
				} else {
					//or next player
					dealerTurn(player);
				}
				
				return;
			}
			
			if(player.getHand().value() > 21) {
				event.editComponents().queue();
				channel.sendMessage(player.getUser().getAsMention() + " busts").queue();
				//if playerNode.next != null
				//	call turn(p) for that player, otherwise it is the dealer's turn
				channel.sendMessage("──────────────────────────────────────").queue();
				
				if(split) {
					turn(player.getSplitHand(), false);
				} else {
					//or next player
					dealerTurn(player);
				}
				
				return;
			}
			
			if(player.getHand().value() == 21) {
				event.editComponents().queue();
				channel.sendMessage(player.getUser().getAsMention() + " has blackjack!").queue();
				//if playerNode.next != null
				//	call turn(p) for that player, otherwise it is the dealer's turn
				channel.sendMessage("──────────────────────────────────────").queue();
				
				if(split) {
					turn(player.getSplitHand(), false);
				} else {
					//or next player
					dealerTurn(player);
				}
				
				return;
			}
			
			ActionRow ar = ActionRow.of(Button.primary("blackjack:hitbutton", "hit"), Button.secondary("blackjack:standbutton", "stand"));
			event.editComponents(ar).queue();
			
			//wait for another hit or stand (then call this method again)
			waiter.waitForEvent(ButtonInteractionEvent.class, 
				e -> {
					return e.getUser().equals(player.getUser()) && e.getMessageIdLong() == event.getMessageIdLong();
				}, 
				e -> { 
					play(player, e, split);
				}, 30, TimeUnit.SECONDS, () -> { 
					channel.sendMessage("due to inactivity, " + player.getUser().getAsMention() + " stands").queue();
					//send to next method
					if(split) {
						turn(player.getSplitHand(), false);
					} else {
						//or next player
						dealerTurn(player);
					}
					return;
				});
			
		}
	}
	
	private void dealerTurn(Player player) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("dealer's turn")
				.addField("current hand", dealer.getHand().toString(), false)
				.addField("hand value", dealer.getHand().value()+"", false)
				.setDescription("please wait")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		channel.sendMessageEmbeds(eb.build()).queue();
		channel.sendMessage("dealer's down card is... " + dealer.getHand().get(1) + "\nhand value: " + dealer.getHand().value()).queue();
		
		//the dealer deals until their hand is greater than or equal to 17
		Card dealingCard;
		while(dealer.getHand().value() < 17) {
			dealingCard = deck.deal();
			dealer.addToHand(dealingCard);
			channel.sendMessage("dealing to DEALER: " + dealingCard + "\nhand value: " + dealer.getHand().value()).queue();
		}
		
		//displays the result of the dealer's turn
		if(dealer.getHand().value() > 21) {
			channel.sendMessage("dealer busts").queue();
		}else if(dealer.hasBlackjack()) {
			channel.sendMessage("dealer has blackjack!").queue();
		}else {
			 channel.sendMessage("dealer stands").queue();
		}
		
		channel.sendMessage("──────────────────────────────────────").queue();
		payout(player);
	}
	
	private void payout(Player player) {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("payout")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
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
		String name = !p.isSplitHand() ? channel.getGuild().getMember(p.getUser()).getEffectiveName() : channel.getGuild().getMember(p.getUser()).getEffectiveName() + "'s split hand";
		
		if(p.getHand().value() > 21) {
			result = String.format("%s has busted. (-$%.2f)", name, p.getWager());
		}else if(dealer.getHand().value() > 21) {
			if(!p.hasBlackjack()) {
				result = String.format("dealer has busted. %s has won. (+$%.2f)", name, p.getWager());
				p.addCash(p.getWager()*2);
			}else {
				result = String.format("dealer has busted. %s has won with blackjack. (+$%.2f)", name, p.getWager()*1.5);
				p.addCash(p.getWager()*2.5);
			}
		}else if(p.getHand().value() == dealer.getHand().value()) {
			result = name + " pushes. (+$0)";
			p.addCash(p.getWager());
		}else if(p.hasBlackjack()) {
			result = String.format("%s has won with blackjack. (+$%.2f)", name, p.getWager()*1.5);
			p.addCash(p.getWager()*2.5);
		}else if(p.getHand().value() > dealer.getHand().value() || p.getHand().size() == 7) {
			result = String.format("%s has won. (+$%.2f)", name, p.getWager());
			p.addCash(p.getWager()*2);
		}else if(p.getHand().value() < dealer.getHand().value()) {
			result = String.format("%s has lost. (-$%.2f)", name, p.getWager());
		}
		
		if(p.getSplitHand() != null) {
			p.getSplitHand().addCash(-p.getSplitHand().getCash());
			result += evaluatePay(p.getSplitHand());
			p.addCash(p.getSplitHand().getCash());
		}
		
		//evaluates pay for side/insurance bet and pays accordingly
		if(p.getSideBet()!=0 && dealer.hasBlackjack() && dealer.getHand().size()==2) {
			result += String.format("\n%s has also bet for insurance and won (+$%.2f)", name, p.getSideBet());
			p.addCash(p.getSideBet()*2);
		}else if(p.getSideBet()!=0) {
			result += String.format("\n%s has also bet for insurance and lost (-$%.2f)", name, p.getSideBet());
		}
		
		return result;
	}
	
	private void insuranceSituation(Player player) {
		
		if(player.getCash() < player.getWager()*0.5) {
			//player can't bet insurance so call insuranceSituation for another player or turn(p head of list)
			turn(player, false);
			return;
		}
		
		channel.sendMessageFormat("%s, how much would you like to bet insurance? ($0.00-$%.2f)", player.getUser().getAsMention(), player.getWager()*0.5).queue();
		waiter.waitForEvent(MessageReceivedEvent.class,
			(e) -> {
				if(e.getAuthor().isBot() || !e.getAuthor().equals(player.getUser())) {
					return false;
				}
				
				try {
					double i = Double.parseDouble(e.getMessage().getContentRaw());
					if(i < 0) {
						channel.sendMessage("bet must be positive").queue();
						return false;
					}
					
					if(i > player.getWager()*0.5) {
						channel.sendMessage("you're insurance bet can only be at most 50% of your original bet (bet 0 if you don't want to bet insurance)").queue();
						return false;
					}
					
				} catch (Exception x){
					channel.sendMessage("please enter a number").queue();
					return false;
				}
				
				return true;
			}, (e) -> {
				player.setSideBet(Double.parseDouble(e.getMessage().getContentRaw()));
				
				//only do this if playerNode.next == null (otherwise call insuranceSituation for the next player)
				channel.sendMessage("dealer is checking face down card...").queue();
				if(dealer.hasBlackjack()) { 
					channel.sendMessage(dealer.getHand().get(1).toString()).queue();
					channel.sendMessage("dealer has blackjack!").queue();
					channel.sendMessage("──────────────────────────────────────").queue();
					payout(player);     
					return;
				}
				
				//if playerNode.next == null and dealer has no blackjack, begin turns 
				turn(player, false);
				return;
			}, 30, TimeUnit.SECONDS, () -> {
				channel.sendMessage("you took too long betting, no side bet set");
				
				//only do this if playerNode.next == null (otherwise call insuranceSituation for the next player)
				channel.sendMessage("dealer is checking face down card...").queue();
				if(dealer.hasBlackjack()) { 
					channel.sendMessage(dealer.getHand().get(1).toString()).queue();
					channel.sendMessage("dealer has blackjack!").queue();
					channel.sendMessage("──────────────────────────────────────").queue();
					payout(player);     
					return;
				}
				
				//if playerNode.next == null and dealer has no blackjack, begin turns 
				turn(player, false);
				return;
		});

			
	}
	
	private void endGame() {
		EmbedBuilder eb = new EmbedBuilder();
		
		if(player.getCash() <= 0.001) {
			channel.sendMessage("you have no money left, ending game...").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			stop();
			return;
		}
			eb.setTitle("round has ended")
				.setDescription("do you wish to continue?")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
				
		channel.sendMessageEmbeds(eb.build()).setActionRow(Button.success("blackjack:continuebutton", "continue"), Button.danger("blackjack:end_game_button", "end game")).queue(msg ->
		waiter.waitForEvent(ButtonInteractionEvent.class, e -> {
			return e.getUser().equals(player.getUser()) && e.getMessageIdLong() == msg.getIdLong();
		},
		e -> {
			if(e.getComponentId().equals("blackjack:continuebutton")) {
				e.editComponents().queue();
				channel.sendMessage("continuing game").queue();
				channel.sendMessage("──────────────────────────────────────").queue();
				run(false);
				return;
			}
			
			if(e.getComponentId().equals("blackjack:end_game_button")) {
				e.editComponents().queue();
				channel.sendMessage("──────────────────────────────────────").queue();
				stop();
				return;
			}
			
		}, 90, TimeUnit.SECONDS, () -> {
			channel.sendMessage("received no response, stopping game").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			stop();
			return;
		}));
	}
	
	public void stop() {
		started = false;
		BlackjackManager.getInstance().getGameManager(channel.getGuild()).started = false;
		
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("thanks for playing blackjack")
				.setDescription("here's how you did")
				.setFooter("by mute | https://github.com/mvte")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		
		//loop for multiple players
		eb.addField(channel.getGuild().getMember(player.getUser()).getEffectiveName(), String.format("$%.2f", player.getCash()), false);
			//should also save to database here (if you do add it)
		
		channel.sendMessageEmbeds(eb.build()).queue();
	}
	
}
