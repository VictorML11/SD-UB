package ub.edu.softwaredistribuit.exceptions;

public class NotEnoughCoinsException extends Exception {

    /**
     * Create a new Error for not having enough coins to bet or start a new game
     */
    public NotEnoughCoinsException() {
        super("ERROR: Not enough coins to bet or start a new Game!");
    }
}
