package ub.edu.softwaredistribuit.commons.players;

import ub.edu.softwaredistribuit.commons.Card;

import java.util.ArrayList;


/**
 * Player of a game, the client.
 */
public class Player extends GamePlayer{

    private int id;
    private int balance;

    /**
     * Creates a new Player with his id
     * @param id PlayerID
     */
    public Player(int id) {
        this.id = id;
    }


    /**
     * Create a new Player with certain hand, id and balance
     * @param hand Hand cards
     * @param id ID
     * @param balance Money
     */
    public Player(ArrayList<Card> hand, int id, int balance) {
        super(hand);
        this.id = id;
        this.balance = balance;
    }

    /**
     * Get the player's current balance
     * @return balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Set player's balance to certain amount
     * @param balance balance to set to
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Checks if player can bet fro the current bet in game
     * @param currentBett Current bet in game
     * @return boolean
     */
    public boolean canBett(int currentBett){
        return currentBett*2 < this.balance;
    }

    /**
     * Subtract money from the player
     * @param currentBett Money lost
     */
    public void substract(int currentBett){
        this.balance -= currentBett;
    }

    /**
     * Add money to the player's balance
     * @param currentBett current bet
     */
    public void add(int currentBett){
        this.balance += currentBett;
    }






}
