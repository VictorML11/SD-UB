package ub.edu.softwaredistribuit.commons;

import java.util.Objects;

/**
 * Game Card of a deck
 */
public class Card {

    private Suit suit;
    private Rank rank;

    /**
     * Creates a new Card with certain suit and rank
     * @param suit Suit
     * @param rank Rank
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }


    /**
     * Get the suit of the card
     * @return Suit
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Get the rank of the card
     * @return Rank
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Get the value of card
     * @return int
     */
    public int getValue(){
        return this.rank.getValue();
    }

    /**
     * Get the Rank of card as char
     * @return char rank
     */
    public char getCharRank(){
        return this.rank.getRank();
    }

    /**
     * Show card as String
     * @return String card representation
     */
    @Override
    public String toString() {
        return rank.getRank() + suit.getUnicode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit &&
                rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}
