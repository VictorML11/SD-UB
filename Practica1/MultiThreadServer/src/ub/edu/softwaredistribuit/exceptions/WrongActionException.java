package ub.edu.softwaredistribuit.exceptions;

/**
 * Error thrown when the client sends to the server an action that can not be
 * taken for the current game state
 */
public class WrongActionException extends Exception {

    /**
     * Create a new Error for the taken action from the client that is not possible
     * @param action Action taken from the client
     */
    public WrongActionException(String action) {
        super("ERROR: The taken action [ " + action + " ] is incorrect for the current GameState!");
    }
}
