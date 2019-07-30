package ub.edu.softwaredistribuit.commons.players;

import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.Deck;

import java.util.ArrayList;


/**
 * Player of a game, the dealer.
 */
public class Dealer extends GamePlayer{


    /**
     * Creates a new Dealer with no hand cards
     */
    public Dealer() {
        super();
    }

    /**
     * Deal the initial cards to a player
     * @param deck Deck to get the cards
     * @param client Client player
     */
    public void dealCards(Deck deck, Player client){
        for(int i = 0; i < 2; i++){
            Card c1 = deck.getTopCard();
            Card c2 = deck.getTopCard();
            client.addCardToHand(c1);
            //Give himself a card
            this.addCardToHand(c2);
        }
    }

    /**
     * Deal one card to a certain player from the deck
     * @param deck Deck to get the card from
     * @param gamePlayer Player to whom we want to deal the card
     * @return Card dealt to the player
     */
    public Card dealNewCardTo(Deck deck, GamePlayer gamePlayer){
        Card c = deck.getTopCard();
        gamePlayer.addCardToHand(c);
        return c;
    }

    /**
     * Check if player hand corresponds to the hand that we have in our data
     * @param receivedCards Cards that player sent us
     * @param gamePlayer Player from we want to check the cards
     * @return boolean
     */
    public boolean checkPlayerHand(ArrayList<Card> receivedCards, GamePlayer gamePlayer){
        if(receivedCards.size() != gamePlayer.getHand().size()) return false;
        return gamePlayer.getHand().containsAll(receivedCards);

    }


    /**
     * Show Dealer's Hand
     */
    @Override
    public void showHand() {
        System.out.print("\nDealer's Hand: ");
        super.showHand();
    }

}
