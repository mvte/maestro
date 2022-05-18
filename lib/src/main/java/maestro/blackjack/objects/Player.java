package maestro.blackjack.objects;

import net.dv8tion.jda.api.entities.User;

public class Player {

	private double cash;
	private double wager;
	private Hand hand;
	private Player splitHand;	//the splithand is of type player as opposed to a hand because hands don't have any wagers attached to them
	private User user;
	private String name;
	private double sideBet;
	private boolean isSplit = false;
	
	public Player() {
		hand = new Hand();
		cash = 0;
	}
	
	public Player(double cash, User user) {
		hand = new Hand();
		this.cash = cash;
		this.user = user;
		name = user.getName();
		sideBet = 0;

	}
	
	//returns the player's name, makes it easy to identify when playing
	public String getName() {
		return name;
	}
	
	//returns the user entity
	public User getUser() {
		return user;
	}

	//returns the amount of cash the player has
	public double getCash() {
		return cash;
	}

	//adds cash to the player
	public void addCash(double sum) {
		cash += sum;
	}

	//returns the players hand
	public Hand getHand() {
		return hand;
	}

	//adds a card to the players hand
	public void addToHand(Card c) {
		hand.add(c);
	}

	//sets the player's main bet
	public void setWager(double bet) {
		wager = bet;
		cash -= bet;
	}
	
	//doubles the players bet (in the event of doubling down)
	public void doubleWager() {
		cash-=wager;
		wager*=2;
	}
	
	//returns the players bet
	public double getWager() {
		return wager;
	}

	//returns if the player has blackjack (their hand is equal to 21)
	public boolean hasBlackjack() {
		return hand.value()==21;
	}
	
	//returns true if the player can split their hand (first and second card are the same and they have enough cash)
	public boolean canSplit() {
		return hand.get(0).equals(hand.get(1)) && cash>=wager;
	}

	//returns the players split hand
	public Player getSplitHand() {
		return splitHand;
	}

	//splits the players hand; the bet for the split hand matches that of the main hand
	public void splitPlayerHand() {
		cash-=wager;
		splitHand = new Player(cash, user);
		splitHand.isSplit = true;
		splitHand.setName(user.getName() + "'s split hand");
		splitHand.setWager(wager);
		
		
		splitHand.addToHand(hand.remove(1));
	}
	
	public boolean isSplitHand() {
		return isSplit;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	//sets insurance bet
	public void setSideBet(double bet) {
		sideBet = bet;
		cash -= bet;
	}
	
	//returns the insurance bet
	public double getSideBet() {
		return sideBet;
	}
	
	//resets the player's hand for replay
	public void resetHand() {
		hand = new Hand();
		splitHand = null;
		sideBet = 0;
	}
	
}
