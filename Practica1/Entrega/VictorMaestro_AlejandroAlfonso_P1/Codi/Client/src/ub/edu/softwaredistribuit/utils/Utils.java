package ub.edu.softwaredistribuit.utils;

import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.players.GamePlayer;

import java.io.IOException;
import java.util.Scanner;

/**
 * Utility class
 */
public class Utils {

    /**
     * Ask for a number method
     * @param sc Scanner
     * @param title Question to ask
     * @return number answer
     */
    public static int askForNumber(Scanner sc, String title) {
        System.out.println(title);
        while (!sc.hasNextInt()) {
            System.out.println("That is not a number!");
            sc.next();
        }
        return sc.nextInt();
    }


    /**
     * Send the current hand to the Server
     * @param gamePlayer Player from we want to send the hand
     * @param comUtils ComUtils instance
     * @throws IOException IOException Error
     */
    public static void sendCurrentHand(GamePlayer gamePlayer, ComUtils comUtils) throws IOException {

        comUtils.write_space();
        comUtils.writeChar((char) Character.forDigit(gamePlayer.getCardsAmount(), 10));

        for (int i = 0; i < gamePlayer.getCardsAmount(); i++) {
            Card c = gamePlayer.getHand().get(i);
            comUtils.writeCard(c);
        }

    }

    /**
     * Generate a random number from two limits up and down
     * @param lower The minimum value that can be generated
     * @param upper The maximum value that can be generated
     * @return Random value that is in the range of lower and upper value
     */
    public static int generateRandomNumber(int lower, int upper){
        return (int) (Math.random() * (upper - lower)) + lower;
    }


}
