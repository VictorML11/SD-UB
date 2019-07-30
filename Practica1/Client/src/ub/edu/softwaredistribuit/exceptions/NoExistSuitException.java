package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when a Card Suit does not exist
 */
public class NoExistSuitException extends Exception {

    /**
     * Create a new Exception with received Suit's Card
     * @param c Received Suit
     */
    public NoExistSuitException(char c) {
        super("ERROR: The Card Suit [ " + c + " ] does not exist!");
    }
}
