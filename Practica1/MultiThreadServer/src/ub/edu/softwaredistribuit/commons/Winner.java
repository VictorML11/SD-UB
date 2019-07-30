package ub.edu.softwaredistribuit.commons;

/**
 * Winner, identifies the Winner of a Game.
 */
public enum Winner {

    CLIENT(),
    SERVER(),
    TIE();

    private int value;

    Winner() {
        this.value = ordinal();
    }

    /**
     * Get the Ordinal Enum Value to know who wins
     * @return Winner value [0, 1 or 2]
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the Winner Enum from a String
     * @param s String
     * @return Winner.class type
     */
    public static Winner getWinnerFromString(String s){
        Winner w = null;
        switch (s){
            case "0":
                return CLIENT;
            case "1":
                return SERVER;
            case "2":
                return TIE;
        }
        return w;
    }
}
