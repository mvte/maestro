package maestro.blackjack.objects;

import net.dv8tion.jda.api.entities.User;

public class Player {

	private double cash;
	private double wager;
	private Hand hand;
	private Player splitHand;	//the splithand is of type player as opposed to a hand because hands don't have any wagers attached to them
	private User user;
	private double sideBet;
	private boolean isSplit = false;
	
	/**
	 * Constructs a player with no hand or cash
	 */
	public Player() {
		hand = new Hand();
		cash = 0;
	}
	
	/**
	 * Constructs a player given the cash amount and the discord User to associate it with
	 * @param cash Amount of cash to give the player
	 * @param user The discord user this Player shall be associated with
	 */
	public Player(double cash, User user) {
		hand = new Hand();
		this.cash = cash;
		this.user = user;
		sideBet = 0;
	}
	
	
	/**
	 * Gets the user entity for this player
	 * @return the user entity
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Gets the amount of cash the player has
	 * @return the amount of cash the player has
	 */
	public double getCash() {
		return cash;
	}

	/**
	 * Adds cash to the player
	 * @param the amount of cash to add to the player
	 */
	public void addCash(double sum) {
		cash += sum;
	}

	/**
	 * Gets the players hand
	 * @return the players hand
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * Adds a card to the players hand
	 * @param c The card to add to the player's hand
	 */
	public void addToHand(Card c) {
		hand.add(c);
	}

	/**
	 * Sets the player's main bet
	 * @param bet the value of the player's bet
	 */
	public void setWager(double bet) {
		wager = bet;
		cash -= bet;
	}
	
	/**
	 * Doubles the players bet (when they double down)
	 */
	public void doubleWager() {
		cash-=wager;
		wager*=2;
	}
	
	/**
	 * Gets the player's bet
	 * @return the player's bet
	 */
	public double getWager() {
		return wager;
	}

	/**
	 * Checks if the player has blackjack (their hand is equal to 21)
	 * @return true if the player has blackjack
	 */
	public boolean hasBlackjack() {
		return hand.value()==21;
	}
	
	/**
	 * Checks if the player can split their hand (first and second card are the same and they have enough cash)
	 * @return true if the player can split their hand
	 */
	public boolean canSplit() {
		return hand.get(0).equals(hand.get(1)) && cash>=wager;
	}

	/**
	 * Gets the player's split hand. The split hand has to be a player object, as opposed to a hand object, as a hand can't have any bet associated with it. Similarly,
	 * a Node takes a Player as it's data.
	 * @return the player's split hand
	 */
	public Player getSplitHand() {
		return splitHand;
	}

	/**
	 * Splits the players hand; the bet for the split hand matches that of the main hand.
	 */
	public void splitPlayerHand() {
		cash-=wager;
		splitHand = new Player(cash, user);
		splitHand.isSplit = true;
		splitHand.setWager(wager);
		
		splitHand.addToHand(hand.remove(1));
	}
	
	/**
	 * Checks if the player is a split hand. 
	 * @return true if the player is a split hand
	 */
	public boolean isSplitHand() {
		return isSplit;
	}
	
	
	/**
	 * Sets insurance bet
	 * @param bet The amount to bet insurance
	 */
	public void setSideBet(double bet) {
		sideBet = bet;
		cash -= bet;
	}
	
	/**
	 * Gets the insurance bet
	 * @return the insurance bet
	 */
	public double getSideBet() {
		return sideBet;
	}
	
	/**
	 * Resets the player's hand for replay. The original hand and splitHand are dereferenced, and the sideBet is set to 0.
	 */
	public void resetHand() {
		hand = new Hand();
		splitHand = null;
		sideBet = 0;
	}
	
}
