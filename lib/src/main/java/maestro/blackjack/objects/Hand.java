package maestro.blackjack.objects;

import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> cards;
	
	/**
	 * Constructs an empty hand
	 */
	public Hand() {
		cards = new ArrayList<>();
	}
	
	/**
	 * Adds a card to the hand.
	 * @param c The card to be added
	 */
	public void add(Card c) {
		cards.add(c);
	}
	
	/**
	 * Adds up the values of all the cards in the hand. If there is an ace, it can be worth 1 or 11, depending on which is most advantageous.
	 * @return the value of the hand
	 */
	public int value() {
		boolean hasAce = false;
		int sum = 0;
		for(Card c: cards) {
			sum += c.getValue();
			if(!hasAce)
				hasAce = c.getValueString().equals("ACE");
		}
		
		//takes care of the soft hand rule
		if(sum<=11 && hasAce)
			sum+=10;
		
		return sum;
	}
	
	/**
	 * Gets the card at index i
	 * @param i the index
	 * @return the card at index i
	 */
	public Card get(int i) {
		return cards.get(i);
	}
	
	/**
	 * Gets the number of cards in the hand
	 * @return the number of cards in the hand
	 */
	public int size() {
		return cards.size();
	}
	
	/**
	 * Removes a card from the hand and returns it (solely for the purpose of split hands)
	 * @param i the index of the card being removed
	 * @return the card being removed
	 */
	public Card remove(int i) {
		return cards.remove(i);
	}
	
	@Override
	public String toString() {
		//e.g. | CARD 1 | CARD 2 | CARD 3 |
		
		String result = "\\| ";
		for(Card c: cards) {
			result += c + " \\| ";
		}
		return result;
	}
	
	/**
	 * A hand is soft if the hand has an ace who's value is 10 (as opposed to 1)
	 * @return whether or not the hand is soft
	 */
	public boolean getSoft() {
		int sum = 0;
		boolean hasAce = false;
		for(Card c : cards) {
			sum += c.getValue();
			if(!hasAce)
				hasAce = c.getValueString().equals("ACE");
		}
		
		return sum<=11 && hasAce;
	}
	
}
