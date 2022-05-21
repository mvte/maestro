package maestro.blackjack.objects;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	private ArrayList<Card> cards;
	private int numDecks = 1;
	
	 
	/**
	 * Constructs a standard 52 card deck
	 * @param shuffle Whether or not to shuffle the deck
	 */
	public Deck(boolean shuffle) {
		cards = new ArrayList<Card>();
		
		for(int suit=1; suit<=4; suit++) {
			for(int value=1; value<=13; value++)
				cards.add(new Card(suit, value));
		}
		
		if(shuffle)
			shuffle();
	}
	
	/**
	 * Constructs a deck of multiple standard size decks
	 * @param shuffle Whether or not to shuffle the deck
	 * @param numDecks The number of decks to create
	 */
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
	
	/**
	 * @return the deck of cards
	 */
	public ArrayList<Card> getDeck() {
		return cards;
	}
	
	/**
	 * Shuffles the deck
	 */
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	/**
	 * Checks if the deck is empty
	 * @return true if the deck is empty (0 cards)
	 */
	public boolean isEmpty() {
		return cards.size() == 0;
	}
	
	/** 
	 * Deals a card and removes it from the deck.
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
