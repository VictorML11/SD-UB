package ub.edu.softwaredistribuit.commons;

/**
 * Game Mode of the game
 */
public enum GameMode {

    MANUAL,
    AUTOMATIC,
    BASIC_STRAT;

    private int mode;

    GameMode() {
        this.mode = ordinal();
    }

    /**
     * Get the game mode value
     * @return mode value
     */
    public int getMode() {
        return mode;
    }


    /**
     * Get the Gamemode Enum from a value
     * @param value int
     * @return GameMode.class type
     */
    public static GameMode fromValue(int value) throws IllegalArgumentException {
        try {
            return GameMode.values()[value];
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unknown GameMode value: "+ value);
        }
    }
}
