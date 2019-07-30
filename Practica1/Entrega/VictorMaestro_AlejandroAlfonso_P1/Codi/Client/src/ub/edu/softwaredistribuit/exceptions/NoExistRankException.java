package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when a Card Rank does not exist
 */
public class NoExistRankException extends Exception {

    /**
     * Create exception with the received Rank Card
     * @param c Received Rank
     */
    public NoExistRankException(char c) {
        super("ERROR: The Card Rank [ " + c + " ] does not exist!");
    }
}
