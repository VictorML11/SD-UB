package ub.edu.softwaredistribuit.exceptions;

/**
 * Error thrown when player tried to cheat on the server cause the cards that he send us
 * are different than the server side has
 */
public class PlayerCheatsException extends Exception {

    /**
     * Create a new Exception for player cards data error
     */
    public PlayerCheatsException() {
        super("ERROR: The received cards are not equal to the server data!");
    }
}
