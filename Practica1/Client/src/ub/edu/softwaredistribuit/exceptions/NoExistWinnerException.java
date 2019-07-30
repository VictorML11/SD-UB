package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when a Winner does not exist
 */
public class NoExistWinnerException extends Exception {

    /**
     * Create a new Exception with the received Winner
     * @param w Received Winner
     */
    public NoExistWinnerException(String w) {
        super("ERROR: The Winner [ " + w + " ] does not exist!");
    }
}
