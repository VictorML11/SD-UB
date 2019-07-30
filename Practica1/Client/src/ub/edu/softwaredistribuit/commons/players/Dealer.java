package ub.edu.softwaredistribuit.commons.players;

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
     * Show Dealer's Hand
     */
    @Override
    public void showHand() {
        System.out.print("\nDealer's Hand: ");
        super.showHand();
    }




}

