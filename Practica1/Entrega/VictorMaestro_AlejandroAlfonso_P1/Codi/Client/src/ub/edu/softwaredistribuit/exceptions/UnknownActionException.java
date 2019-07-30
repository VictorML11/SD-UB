package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when the action taken is not known
 */
public class UnknownActionException extends Exception {

    /**
     * Creates a new Exception with certain action that is not known by the client
     * @param action Action that is not known by the client
     */
    public UnknownActionException(String action) {
        super("ERROR: The taken action [ " + action + " ] is not known!");
    }
}
