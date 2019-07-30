package ub.edu.softwaredistribuit.commons;

/**
 * Available commands that can be used in the communication with the server
 */
public enum Command {

    STRT,
    CASH,
    HITT,
    SHOW,
    BETT,
    SRND,
    RPLY,
    EXIT,
    INIT,
    IDCK,
    CARD,
    WINS,
    ERRO;



    /**
     * Get the Command Enum from a String
     * @param cmd String
     * @return Command.class type
     */
    public static Command getCommandFromString(String cmd) {
        Command command = null;
        switch (cmd){
            case "STRT":
                return STRT;
            case "CASH":
                return CASH;
            case "HITT":
                return HITT;
            case "SHOW":
                return SHOW;
            case "BETT":
                return BETT;
            case "SRND":
                return SRND;
            case "RPLY":
                return RPLY;
            case "EXIT":
                return EXIT;
            case "INIT":
                return INIT;
            case "IDCK":
                return IDCK;
            case "CARD":
                return CARD;
            case "WINS":
                return WINS;
            case "ERRO":
                return ERRO;
        }
        return command;
    }

}
