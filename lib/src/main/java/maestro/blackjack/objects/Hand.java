package maestro.blackjack.objects;

import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> cards;
	
	//default constructor
	public Hand() {
		cards = new ArrayList<>();
	}
	
	//adds a card to the hand
	public void add(Card c) {
		cards.add(c);
	}
	
	//returns the value of the hand
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
	
	//returns the card at index i
	public Card get(int i) {
		return cards.get(i);
	}
	
	//returns the number of cards in the hand
	public int size() {
		return cards.size();
	}
	
	//removes a card from the hand and returns it (solely for the purpose of split hands)
	public Card remove(int i) {
		return cards.remove(i);
	}
	
	//displays all cards in the hand
	public String toString() {
		String result = "\\| ";
		for(Card c: cards) {
			result += c + " \\| ";
		}
		return result;
	}
	
	//a hand is soft if the hand has an ace who's value is 10 (as opposed to 1)
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
