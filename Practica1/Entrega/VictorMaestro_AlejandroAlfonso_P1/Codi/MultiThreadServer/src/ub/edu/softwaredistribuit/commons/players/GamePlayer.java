package ub.edu.softwaredistribuit.commons.players;

import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.Rank;

import java.util.ArrayList;

public class GamePlayer {

    private ArrayList<Card> hand;

    /**
     * Creates a new GamePlayer's instance with certain hand
     * @param hand Player hand cards
     */
    public GamePlayer(ArrayList<Card> hand) {
        this.hand = hand;
    }


    /**
     * Creates a new GamePlayer's instance with no hand cards
     */
    public GamePlayer() {
        this.hand = new ArrayList<>();
    }

    /**
     * Get current Game player's hand cards
     * @return ArrayList with Cards of the player
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Add a card to the player's hand
     * @param card Deck card
     */
    public void addCardToHand(Card card){
        this.hand.add(card);
    }

    /**
     * Remove the Player's hand
     */
    public void clearHand(){
        this.hand.clear();
    }

    /**
     * Show the current player's cards
     */
    public void showHand(){
        for(Card c : hand){
            System.out.print(" " + c.toString());
        }

        String handInfo = "\t HandValue: " + this.getHandValue();

        if(this.hasBusted()){
            handInfo += "(Busted)";
        }else if(this.hasBlackJax()){
            handInfo += "(BlackJack)";
        }

        System.out.println(handInfo);
    }

    /**
     * Get the current hand value from the player's cards
     * @return int
     */
    public int getHandValue(){
        int currentHandValue = 0;

        int numberAces = 0;

        for(Card c : hand){

            if(c.getRank() == Rank.ACE){
                numberAces++;
                continue;
            }
            currentHandValue += c.getValue();
        }

        boolean added = false;
        for(int i = 0; i < numberAces; i++){
            int aceVal = 11;
            if(aceVal + currentHandValue > 21 && !added){
                aceVal = 1;
                added = true;
            }
            currentHandValue += aceVal;
        }


        return currentHandValue;
    }


    /**
     * Check if the player hand is an initial hand
     * @return boolean
     */
    public boolean isInitialHand() {
        return this.getCardsAmount() == 2;
    }

    /**
     * Gets the amount of cards the player has in his hand
     * @return int cards amount
     */
    public int getCardsAmount() {
        return this.hand.size();
    }

    /**
     * Checks if player has either busted or not
     * @return boolean
     */
    public boolean hasBusted() {
        return this.getHandValue() > 21;
    }

    /**
     * Check if player has BlackJack in the initial hand
     * @return boolean
     */
    public boolean hasBlackJax() {
        return this.getHandValue() == 21 && this.isInitialHand();
    }

    /**
     * Check if player has 21 but is not blackJack
     * @return boolean
     */
    public boolean has21() {
        return this.getHandValue() == 21 && !this.isInitialHand();
    }
}
