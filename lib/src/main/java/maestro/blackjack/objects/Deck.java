package maestro.blackjack.objects;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	private ArrayList<Card> cards;
	private int numDecks = 1;
	
	 
	//Constructs a deck of 52 cards, and will shuffle depending on the parameters.
	public Deck(boolean shuffle) {
		cards = new ArrayList<Card>();
		
		for(int suit=1; suit<=4; suit++) {
			for(int value=1; value<=13; value++)
				cards.add(new Card(suit, value));
		}
		
		if(shuffle)
			shuffle();
	}
	
	// Constructs a deck of multiple standard size decks
	public Deck(boolean shuffle, int numDecks) {
		cards = new ArrayList<Card>();
		this.numDecks = numDecks;
		
		for(int i = 0; i < numDecks; i++) {
			for(int suit = 1; suit <=4; suit++)
				for(int value = 1; value <= 13; value++)
					cards.add(new Card(suit, value));
		}
		
		if(shuffle)
			shuffle();
	}
	
	//returns the deck
	public ArrayList<Card> getDeck() {
		return cards;
	}
	
	//shuffles the deck
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	//returns true if there are no cards in the deck
	public boolean isEmpty() {
		return cards.size() == 0;
	}
	
	/** Deals a card and removes it from the deck.
	 * If the deck is empty, then a new deck will be created.
	 * @return Card the dealt card
	 */
	public Card deal() {
		if(isEmpty()) {
			System.out.println("Deck is Empty! Creating new deck");
			 Deck d = new Deck(true, numDecks);
			 cards = d.getDeck();
			 return cards.remove(0);
		}else {
			return cards.remove(0);
		}
	}
}
