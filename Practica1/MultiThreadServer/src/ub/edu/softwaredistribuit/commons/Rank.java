package ub.edu.softwaredistribuit.commons;

/**
 * Card Rank
 */
public enum Rank {

    ACE('A', 11),
    KING('K', 10),
    QUEEN('Q', 10),
    JACK('J', 10),
    TEN('X', 10),
    NINE('9', 9),
    EIGHT('8', 8),
    SEVEN('7', 7),
    SIX('6', 6),
    FIVE('5', 5),
    FOUR('4', 4),
    THREE('3', 3),
    TWO('2', 2);


    private char rank;
    private int value;

    Rank(char rank, int value) {
        this.rank = rank;
        this.value = value;
    }

    /**
     * Get the rank symbol of the card
     * @return rank of card
     */
    public char getRank() {
        return rank;
    }

    /**
     * Get the value of a card rank
     * @return value of card
     */
    public int getValue() {
        return value;
    }


    /**
     * Get the Rank Enum from a char
     * @param ch char
     * @return Rank.class type
     */
    public static Rank getRankFromChar(char ch){
        switch (ch){
            case 'A':
                return ACE;
            case 'K':
                return KING;
            case 'Q':
                return QUEEN;
            case 'J':
                return JACK;
            case 'X':
                return TEN;
            case '9':
                return NINE;
            case '8':
                return EIGHT;
            case '7':
                return SEVEN;
            case '6':
                return SIX;
            case '5':
                return FIVE;
            case '4':
                return FOUR;
            case '3':
                return THREE;
            case '2':
                return TWO;

            default:
                return null;
        }
    }

}
