package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when a Server loses the track of the game state
 */
public class ServerSideErrorException extends Exception {

    /**
     * Create a new exception for the current game state error
     */
    public ServerSideErrorException() {
        super("Error: There was an error in the server state tracker!");
    }
}
