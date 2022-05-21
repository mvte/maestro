package maestro.blackjack.objects;

public class Card {
	
	private int suit;	//1: spades - 2: hearts - 3: diamonds - 4: clubs
	private int value;	//1-13
	
	public Card(int suit, int value) {
		this.suit = suit;
		this.value = value;
	}
	
	/**
	 * Corresponds the suit number to its actual name (should probably have been done with an enum, but oh well)
	 * @return The suit of the card
	 */
	public String getSuit() {
		switch(suit) {
			case 1:
				return "SPADES";
			case 2: 
				return "HEARTS";
			case 3: 
				return "DIAMONDS";
			case 4:
				return "CLUBS";
		}
		
		return null;
	}
	
	
	/**
	 * Gets the numerical value of the card. For blackjack purposes, all cards whose rank is above 10 becomes evaluated as 10
	 * @return the numerical value of the card
	 */
	public int getValue() {
		if(value >= 10)
			return 10;
		else
			return value;
	}
	
	/**
	 * Not a proper test of equality, as it only compares the card's rank (and not suit)
	 * @param c The card being compared
	 * @return true if this card's rank (not value) is equal to c's
	 */
	public boolean equals(Card c) {
		return this.getValueString().equals(c.getValueString());
	}
	
	/**
	 * Gets the rank of the card 
	 * @return the rank of the card
	 */
	public String getValueString() {
		if(value == 1)
			return "ACE";
		else if(value == 11)
			return "JACK";
		else if(value == 12)
			return "QUEEN";
		else if(value == 13)
			return "KING";
		else
			return value+"";
	}
	
	@Override
	public String toString() {
		//e.g. ACE of SPADES
		return "*" + getValueString() + " of " +  getSuit() + "*";
	}

}
