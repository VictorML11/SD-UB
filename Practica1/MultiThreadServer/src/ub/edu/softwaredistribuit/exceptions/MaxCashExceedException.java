package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when Client exceeds max cash
 */
public class MaxCashExceedException extends Exception {

    /**
     * Create exception for max cash
     */
    public MaxCashExceedException() {
        super("Error: Exceeding the maximum cash for this game!");
    }
}
