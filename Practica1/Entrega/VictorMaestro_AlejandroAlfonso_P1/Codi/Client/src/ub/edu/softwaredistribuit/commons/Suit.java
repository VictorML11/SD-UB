package ub.edu.softwaredistribuit.commons;

/**
 * Card Suit
 */
public enum Suit {

    HEARTS('\03', "\u2665"),
    DIAMONDS('\04', "\u2666"),
    CLUBS('\05', "\u2663"),
    SPADES('\06', "\u2660");

    private char ascii;
    private String unicode;

    Suit(char ascii, String unicode) {
        this.ascii = ascii;
        this.unicode = unicode;
    }

    /**
     * Get the Ascii representation of the Suit
     * @return ascii code
     */
    public char getAscii() {
        return ascii;
    }

    /**
     * Get the Unicode representation of the suit
     * @return unicode
     */
    public String getUnicode() {
        return unicode;
    }


    /**
     * Get the Suit Enum from a Char
     * @param c char
     * @return Suit.class type
     */
    public static Suit getSuitFromAscii(char c){
        switch (c){

            case '\03': case '3':
                return HEARTS;
            case '\04': case '4':
                return DIAMONDS;
            case '\05': case '5':
                return CLUBS;
            case '\06': case'6':
                return SPADES;
            default:
                return null;
        }
    }
}
