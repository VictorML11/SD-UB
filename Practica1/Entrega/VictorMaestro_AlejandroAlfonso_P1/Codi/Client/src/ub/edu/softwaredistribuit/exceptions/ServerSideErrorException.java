package ub.edu.softwaredistribuit.exceptions;

/**
 * Exception thrown when a Server sent to the client an error
 */
public class ServerSideErrorException extends Exception {

    /**
     * Create a new Exception with the Error send from the server
     * @param err Error Thrown by the server
     */
    public ServerSideErrorException(String err) {
        super("Server Error: "  + err);
    }
}
