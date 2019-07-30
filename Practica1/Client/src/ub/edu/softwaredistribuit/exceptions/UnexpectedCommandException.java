package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when a Command from the server was not expected
 */
public class UnexpectedCommandException extends Exception {

    /**
     * Creates a new Exception with the Command received from the server that was not expected
     * @param s Command received
     */
    public UnexpectedCommandException(String s) {
        super("ERROR: Unexpected received Command [ " + s + " ] for current GameState");
    }
}
