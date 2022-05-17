package maestro.blackjack.objects;

public class Card {
	
	private int suit;	//1: spades - 2: hearts - 3: diamonds - 4: clubs
	private int value;	//1-13
	
	public Card(int suit, int value) {
		this.suit = suit;
		this.value = value;
	}
	
	//corresponds the suit number to its actual name and returns that
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
	 * I've made two different getter methods for the value of the card. The first one simply returns an int, easy
	 * for calculating value of hand. The other method is for displaying the card and for detecting an ace or a
	 * split hand.
	 */
	
	//returns value
	public int getValue() {
		if(value >= 10)
			return 10;
		else
			return value;
	}
	
	//returns true if the card's face (not value) is equal to @param c's
	public boolean equals(Card c) {
		return this.getValueString().equals(c.getValueString());
	}
	
	//returns value but with true naming
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
	
	//e.g. ACE of SPADES
	public String toString() {
		return getValueString() + " of " +  getSuit();
	}

}
