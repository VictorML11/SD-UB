package ub.edu.softwaredistribuit.commons;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Game deck
 */
public class Deck {

    private ArrayList<Card> deck;

    /**
     * Create a new Game deck full of cards
     */
    public Deck() {
        deck = new ArrayList<>();
        this.generateDeck();

    }

    /**
     * Generate a new Deck cards 52 in total 4 Suits of 13 Ranks each
     */
    private void generateDeck(){
        for(int i = 0; i < Suit.values().length; i++){
            for (int j = 0; j < Rank.values().length; j++){
                Suit s = Suit.values()[i];
                Rank r = Rank.values()[j];
                deck.add(new Card(s, r));
            }
        }
        this.shuffleDeck();
        this.shuffleDeck();
    }

    /**
     * Get the top card from the deck
     * @return Card
     */
    public Card getTopCard(){
        return deck.remove(0);
    }


    /**
     * Shuffle the deck
     */
    public void shuffleDeck(){
        Collections.shuffle(this.deck);
    }

    /**
     * Show the whole deck cards
     */
    public void showDeck(){
        for(int i = 0; i < this.deck.size(); i++){
            System.out.println(deck.get(i));
        }
    }
}
