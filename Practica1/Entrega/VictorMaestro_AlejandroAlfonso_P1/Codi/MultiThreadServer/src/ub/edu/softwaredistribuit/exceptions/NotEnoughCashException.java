package ub.edu.softwaredistribuit.exceptions;

/**
 * Error thrown when player's initial bet is lower than the minium
 */
public class NotEnoughCashException extends Exception {

    /**
     * Create a new Error for not enough cash
     */
    public NotEnoughCashException() {
        super("ERROR: The MaxCash should be bigger than the initial bet");
    }
}
