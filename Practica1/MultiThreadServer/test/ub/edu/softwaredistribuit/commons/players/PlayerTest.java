package ub.edu.softwaredistribuit.commons.players;

import org.junit.jupiter.api.Test;
import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.Rank;
import ub.edu.softwaredistribuit.commons.Suit;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {


    /**
     * Test to check if player ACEs counts as 11 or 1 correctly
     */
    @Test
    void handValueAceTest() {

        int handValueTest = 18+11;

        Card c1 = new Card(Suit.CLUBS, Rank.TWO);
        Card c2 = new Card(Suit.HEARTS, Rank.ACE);
        Card c3 = new Card(Suit.CLUBS, Rank.TWO);
        Card c4 = new Card(Suit.HEARTS, Rank.ACE);
        Card c5 = new Card(Suit.CLUBS, Rank.TWO);
        Card c6 = new Card(Suit.CLUBS, Rank.ACE);

        ArrayList<Card> hand = new ArrayList<>(Arrays.asList(c1,c2,c3,c4,c5,c6));

        Player p = new Player(hand,1, 100);

        assertEquals(handValueTest, p.getHandValue());

    }


}