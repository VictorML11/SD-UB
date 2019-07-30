package ub.edu.softwaredistribuit.exceptions;

/**
 * Error thrown when a client sends a Command that is not known by the server
 */
public class NotClientCommandException extends Exception {

    /**
     * Create a new Exception for Client received command
     * @param cmd Command received
     */
    public NotClientCommandException(String cmd) {
        super("ERROR: You can not perform the [ " + cmd + " ] action!");
    }
}
