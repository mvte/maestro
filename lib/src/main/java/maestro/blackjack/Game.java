package maestro.blackjack;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import maestro.Bot;
import maestro.blackjack.LinkedList.Node;
import maestro.blackjack.objects.Card;
import maestro.blackjack.objects.Deck;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Game {
	
	/**
	 * TODO
	 * - multiplayer!! (DONE :D)
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
	 * - minimize lag, or add a typing queue where it happens (idk if this is possible)
	 * - replace getNickname() with getEffectiveName() (just in case the user has no nickname) (DONE)
	 * - fix splithand and insurance bet compatibility (DONE)
	 * 		- separate the split situation into it's own method, and call it in both dealToPlayers() and insuranceSituation()
	 * - game doesn't stop properly when stop command is called
	 * - split hand has to remove itself from linked list (this means you have to keep track of prev node) (DONE)
	 * - clear linkedlist when game finishes (DONE)
	 * - when all players have ran out of money, async bugs occur (fixed)
	 * - if a game is occuring on two servers, the bot will take the same input from the same person on both servers
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
	 * 		to be able to have access to the nodes of a linked list, you have to code your own linkedlist class
	 * 
	 * 
	 *
	 */
	
	private LinkedList players;
	private Node firstPlayerNode;
	private Deck deck;
	private Player dealer;
	private TextChannel channel;
	private EventWaiter waiter = Bot.waiter;
	public boolean started = false;
	
	public Game(int numDecks, TextChannel channel, ArrayList<Player> players) {
		this.players = new LinkedList(players);
		firstPlayerNode = this.players.getHeadNode();
		deck = new Deck(true, numDecks);
		this.channel = channel;
		dealer = new Player();
	}
	
	//treat this like the actual game
	public void run(boolean first) {
		dealer.resetHand();
		EmbedBuilder eb = new EmbedBuilder();
		
		
		for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) { 
			ptr.getPlayer().resetHand();
			eb.addField(channel.getGuild().getMember(ptr.getPlayer().getUser()).getEffectiveName(), String.format("$%.2f", ptr.getPlayer().getCash()), false);
		}
		
		eb
				.setTitle("current balances")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		channel.sendMessageEmbeds(eb.build()).queue();
		
		//because of the way eventwaiter works, we need to have everything linked starting from this method
		setBet(firstPlayerNode);
	
		
	}
	
	/**
	 * Takes input from a player to determine their bet
	 * @param player The player we're accepting 
	 */
	private void setBet(Node playerNode) {
		//you should recursively call this method for every player in the game if you plan to add multiplayer
		//	the players should be kept in a linked list
		//something like
		//	if(playerNode.next == null) nextMethod();
		//  else setBet(playerNode.next());
		
		Player player = playerNode.getPlayer();
		
		channel.sendMessage(player.getUser().getAsMention() + ", please enter your bet").queue();
		waiter.waitForEvent(MessageReceivedEvent.class, 
			(e) -> {
				if(e.getAuthor().isBot() || !e.getAuthor().equals(player.getUser()) || !e.getTextChannel().equals(channel)) {
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
				if(playerNode.next == null)
					dealToPlayers();
				else 
					setBet(playerNode.next);
			}, 30, TimeUnit.SECONDS, () -> {
				channel.sendMessage("you took too long betting, default bet set (0)").queue();
				player.setWager(0);
				//send to next method
				if(playerNode.next == null)
					dealToPlayers();
				else 
					setBet(playerNode.next);
			});
		
	}
	
	private void dealToPlayers() {
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("dealing")
				.setDescription("maestro is dealing cards, please wait")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		channel.sendMessageEmbeds(eb.build()).queue();
		
		//a card is dealt to every player once (including the dealer) before dealing the second card to all players
		for(int i = 0; i < 2; i++) {
			Card dealingCard;
			
			//deal to player (for multiple players, you should loop)
			for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) {
				dealingCard = deck.deal();
				Player player = ptr.getPlayer();
				channel.sendMessage("dealing to " + player.getUser().getAsMention() + ": " + dealingCard.toString()).queue();
				player.getHand().add(dealingCard);
			}
			
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
			for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) { 
				Player player = ptr.getPlayer();	
				
				if(!eligible)		
					eligible = player.getCash() > 0;
			}
			
			if(eligible) {
				channel.sendMessage("the dealer has been dealt an ace, some players are eligible for insurance").queue();
				insuranceSituation(firstPlayerNode);
				return;
			}
			
			splitSituation(firstPlayerNode);
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
			payout();     
			return;
		}
		
		channel.sendMessage("──────────────────────────────────────").queue();
		splitSituation(firstPlayerNode);
		
	}
	
	private void splitSituation(Node playerNode) {
		Player player = playerNode.getPlayer();
		
		//ask here what the player would like to do if they want to split hands
		//	iterate through list for multiple players
		if(player.canSplit()) {
			EmbedBuilder ebSpl = new EmbedBuilder()
					.setTitle("split hand")
					.setDescription(channel.getGuild().getMember(player.getUser()).getEffectiveName() + ", you are able to split your hand. this split hand will have the same bet as your original bet. would you like to split?")
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
						Node splitNode = new Node(player.getSplitHand(), playerNode.next);
						playerNode.next = splitNode;
						splitNode.prev = playerNode;
						
						if(splitNode.next == null) 
							turn(firstPlayerNode);
						else
							splitSituation(splitNode.next);
					}
					
					if(e.getComponentId().equals("blackjack:no_split_button")) {
						if(playerNode.next == null) 
							turn(firstPlayerNode);
						else
							splitSituation(playerNode.next);
					}
				}, 30, TimeUnit.SECONDS, () -> {
					channel.sendMessage("you took too long, will not split hand").queue();
					if(playerNode.next == null) 
						turn(firstPlayerNode);
					else
						splitSituation(playerNode.next);
				}));
		} else {
			if(playerNode.next == null) 
				turn(firstPlayerNode);
			else
				splitSituation(playerNode.next);
		}
	}
	
	private void insuranceSituation(Node playerNode) {
		Player player = playerNode.getPlayer();
		
		if(player.getCash() <= 0) {
			//player can't bet insurance so call insuranceSituation for another player or turn(p head of list)
			if(playerNode.next == null)
				splitSituation(firstPlayerNode);
			else
				insuranceSituation(playerNode.next);
			return;
			
		}
		double maxSideBet = Math.min(player.getCash(), player.getWager()*0.5);
		channel.sendMessageFormat("%s, how much would you like to bet insurance? ($0.00-$%.2f)", player.getUser().getAsMention(), maxSideBet).queue();
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
				
				if(playerNode.next == null) {
					channel.sendMessage("dealer is checking face down card...").queue();
					if(dealer.hasBlackjack()) { 
						channel.sendMessage(dealer.getHand().get(1).toString()).queue();
						channel.sendMessage("dealer has blackjack!").queue();
						channel.sendMessage("──────────────────────────────────────").queue();
						payout();     
						return;
					}
					turn(firstPlayerNode);
					return;
				}
				
				//if playerNode.next == null and dealer has no blackjack, begin turns, otherwise, insurance bet for next player
				insuranceSituation(playerNode.next);
				return;
			}, 30, TimeUnit.SECONDS, () -> {
				channel.sendMessage("you took too long betting, no side bet set");
				
				if(playerNode.next == null) {
					channel.sendMessage("dealer is checking face down card...").queue();
					if(dealer.hasBlackjack()) { 
						channel.sendMessage(dealer.getHand().get(1).toString()).queue();
						channel.sendMessage("dealer has blackjack!").queue();
						channel.sendMessage("──────────────────────────────────────").queue();
						payout();     
						return;
					}
					
					turn(firstPlayerNode);
					return;
				}
				
				//if playerNode.next == null and dealer has no blackjack, begin turns, otherwise, insurance bet for next player
				insuranceSituation(playerNode.next);
		});
			
	}

	private void turn(Node pNode) {
		//again, same deal as setBets: recursively call this function for each player
		//	when a player stands, call turn(playerNode.next()) if the next player is not null, otherwise go to the next method
		//channel.getGuild().retrieveMemberById(p.getUser().getId()).queue();
		Player p = pNode.getPlayer();
		
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
			
			if(pNode.next == null) {
				dealerTurn();
			} else {
				turn(pNode.next);
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
						return e.getUser().equals(p.getUser()) && e.getMessageIdLong() == m.getIdLong();
					},
					(e) -> {
						play(pNode, e);
						
						//call next method or call this method again (for a different player)
						
					}, 30, TimeUnit.SECONDS, () -> {
						channel.sendMessage("due to inactivity, " + p.getUser().getAsMention() + " stands").queue();
						//send to next method (dealer turn, player turn, or player's split hand turn)
						dealerTurn();
					}));
			
	}
	
	
	private void play(Node playerNode, ButtonInteractionEvent event) {
		Player player = playerNode.getPlayer();
		
		//recursively call play until the player busts or stands
		if(event.getComponentId().equals("blackjack:standbutton")) {
			event.editComponents().queue();
			channel.sendMessage(player.getUser().getAsMention() + " stands").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			
			if(playerNode.next == null) {
				dealerTurn();
			} else {
				turn(playerNode.next);
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
			
			if(playerNode.next == null) {
				dealerTurn();
			} else {
				turn(playerNode.next);
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
				
				if(playerNode.next == null) {
					dealerTurn();
				} else {
					turn(playerNode.next);
				}
				
				return;
			}
			
			if(player.getHand().value() > 21) {
				event.editComponents().queue();
				channel.sendMessage(player.getUser().getAsMention() + " busts").queue();
				//if playerNode.next != null
				//	call turn(p) for that player, otherwise it is the dealer's turn
				channel.sendMessage("──────────────────────────────────────").queue();
				
				if(playerNode.next == null) {
					dealerTurn();
				} else {
					turn(playerNode.next);
				}
				
				return;
			}
			
			if(player.getHand().value() == 21) {
				event.editComponents().queue();
				channel.sendMessage(player.getUser().getAsMention() + " has blackjack!").queue();
				//if playerNode.next != null
				//	call turn(p) for that player, otherwise it is the dealer's turn
				channel.sendMessage("──────────────────────────────────────").queue();
				
				if(playerNode.next == null) {
					dealerTurn();
				} else {
					turn(playerNode.next);
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
					play(playerNode, e);
				}, 30, TimeUnit.SECONDS, () -> { 
					channel.sendMessage("due to inactivity, " + player.getUser().getAsMention() + " stands").queue();
					//send to next method
					if(playerNode.next == null) {
						dealerTurn();
					} else {
						turn(playerNode.next);
					}
					return;
				});
			
		}
	}
	
	private void dealerTurn() {
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
		payout();
	}
	
	private void payout() {
		clearSplits();
		
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("payout")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
		for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) {
			Player player = ptr.getPlayer();
			eb.addField(channel.getGuild().getMember(player.getUser()).getEffectiveName(), evaluatePay(player), false);
		}
		
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
			result += "\n" + evaluatePay(p.getSplitHand());
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
	
	private void clearSplits() {
		for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) { 
			//should be fine because the firstPlayerNode should never be a split hand
			if(ptr.getPlayer().isSplitHand()) {
				ptr.prev.next = ptr.next;
			}
		}
	}

	private void endGame() {
		EmbedBuilder eb = new EmbedBuilder();
		
//		outdated, instead remove players who have zero cash (you can do this later or add a separate method in the linkedlist class
		boolean allPlayersBroke = clearBrokePlayers();
		
		if(allPlayersBroke) {
			channel.sendMessage("all players have ran out of money, ending game").queue();
			channel.sendMessage("──────────────────────────────────────").queue();
			stop();
			return;
		}
		
		eb.setTitle("round has ended")
			.setDescription("do you wish to continue?")
			.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl());
				
		channel.sendMessageEmbeds(eb.build()).setActionRow(Button.success("blackjack:continuebutton", "continue"), Button.danger("blackjack:end_game_button", "end game")).queue(msg ->
		waiter.waitForEvent(ButtonInteractionEvent.class, e -> {
			return e.getUser().equals(firstPlayerNode.getPlayer().getUser()) && e.getMessageIdLong() == msg.getIdLong();
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
	
	/**
	 * 
	 * @return
	 */
	private boolean clearBrokePlayers() {
		for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) {
			if(ptr.getPlayer().getCash() <= 0.001) {
				channel.sendMessage(ptr.getPlayer().getUser().getAsMention() +  " has lost all their money, removing them from the game").queue();
				
				if(ptr == firstPlayerNode) {
					if(firstPlayerNode.next == null) {
						return true;
					}
					
					firstPlayerNode = firstPlayerNode.next;
					break;
				}
				
				if(ptr.next != null)
					ptr.next.prev = ptr.prev;
					
				ptr.prev.next = ptr.next;
			}
		}
		
		return false;
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
		for(Node ptr = firstPlayerNode; ptr != null; ptr = ptr.next) {
			Player player = ptr.getPlayer();
			eb.addField(channel.getGuild().getMember(player.getUser()).getEffectiveName(), String.format("$%.2f", player.getCash()), false);
			//should also save to database here (if you do add it)
		}
			
		channel.sendMessageEmbeds(eb.build()).queue();
		
		this.players = new LinkedList();
		firstPlayerNode = null;
		BlackjackManager.getInstance().getGameManager(channel.getGuild()).nullGame();
	}
	
}
