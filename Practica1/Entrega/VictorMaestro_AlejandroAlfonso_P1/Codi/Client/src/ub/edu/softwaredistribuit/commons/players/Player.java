package ub.edu.softwaredistribuit.commons.players;

/**
 * Player of a game, the client.
 */
public class Player extends GamePlayer{

    private int id;
    private int balance;

    /**
     * Creates a new Player with his id
     * @param id PlayerID
     */
    public Player(int id) {
        super();
        this.id = id;
    }


    /**
     * Set player's balance to certain amount
     * @param balance balance to set to
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Get the player's current balance
     * @return balance
     */
    public int getBalance() {
        return balance;
    }


    /**
     * Add money to the player's balance
     * @param currentBett current bet
     */
    public void add(int currentBett){
        this.balance += currentBett;
    }

    /**
     * Checks if player can bet fro the current bet in game
     * @param currentBett Current bet in game
     * @return boolean
     */
    public boolean canBett(int currentBett){
        return currentBett*2 < this.balance;
    }

    /**
     * Subtract money from the player
     * @param currentBett Money lost
     */
    public void subtract(int currentBett){
        this.balance -= currentBett;
    }

    /**
     * Show Client's Hand
     */
    @Override
    public void showHand() {
        System.out.print("\nYour Current Hand: ");
        super.showHand();
    }
}
