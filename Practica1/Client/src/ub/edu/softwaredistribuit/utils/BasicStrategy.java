package ub.edu.softwaredistribuit.utils;

import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.Command;
import ub.edu.softwaredistribuit.commons.players.GamePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * BasicStrategy actions are taken more with certain standard
 */
public class BasicStrategy {

    private HashMap<Integer,ArrayList<Command>> valueOptions = new HashMap<>();
    private HashMap<Integer,ArrayList<Command>> softOptions = new HashMap<>();
    private CustomMenu menu;

    private boolean endOfGame;

    /**
     * Create basic strategy instance from certain menu options
     * @param menu to take the actions from
     */
    public BasicStrategy(CustomMenu menu) {
        this.menu = menu;
        this.endOfGame = false;
        fillValueOptions();
        fillSoftOptions();
    }


    /**
     * Select the best option following the Wiki's table
     * @param player Player Client
     * @param dealer Dealer of the game
     * @return Command the best option taken
     */
    public Command selectBestOption(GamePlayer player, GamePlayer dealer){

        // First check if is the start of the game or if the player doesn't have cards
        if(player == null || dealer == null || !player.hasCards()){
            return menu.selectRandomAction();
        }

        // If is the end of the game we select a random option
        if(endOfGame) return menu.selectRandomAction();

        // We already know that if the player busted we are forced to stand!
        if(player.hasBusted()){
            return Command.SHOW;
        }

        // If player has BlackJack or has 21 we want to optimize as much as possible
        // So we will do a BETT first if possible and then SHOW
        /*
            - BlackJack contemplates the A + (K,Q,J,X)
            - 21 Contemplates the case we are very sure we win
         */
        if(player.hasBlackJax() || player.has21()){
            if(menu.availableCmds().contains(Command.BETT)){
                return Command.BETT;
            }else{
                return Command.SHOW;
            }
        }

        Card card = player.getNonSoftCard(); //Get the Non-Ace card if the player has

        int playerValue;
        int dealerValue = dealer.getHand().get(0).getValue();
        int idx = dealerValue - 2;

        ArrayList<Command> possibleCmds = menu.availableCmds();
        Command bestOption;

        // If card is null it means we have Double ACE it should go to the upper side of the table
        // and work with handvalue
        if(card == null){
            playerValue = player.getHandValue();
            bestOption = valueOptions.get(playerValue).get(idx);
        }else{
            playerValue = card.getValue();
            bestOption = softOptions.get(playerValue).get(idx);
        }

        // We check first if the command we selected is possible to be done
        if(possibleCmds.contains(bestOption)){
            return bestOption;
        }

        // If we couldn't do the best option then take the Wiki's next best option
        if(bestOption == Command.SRND){
            if(possibleCmds.contains(Command.HITT)){
                return Command.HITT;
            }
        } else if(bestOption == Command.BETT){

            if(playerValue == 7 || playerValue == 8){
                if(possibleCmds.contains(Command.SHOW)){
                    return Command.SHOW;
                }
            }else{
                if(possibleCmds.contains(Command.HITT)){
                    return Command.HITT;
                }
            }
        }

        // For every other actions that were not contemplated select a randomOption
        return menu.selectRandomAction();
    }



    private void fillValueOptions() {
        // Fill all the Value options that can be taken
        valueOptions.put(20, generateRowof(new ArrayList<>(Arrays.asList(10)), new ArrayList<>(Arrays.asList(Command.SHOW))));
        valueOptions.put(19, valueOptions.get(20));
        valueOptions.put(18, valueOptions.get(20));
        valueOptions.put(17, valueOptions.get(20));
        valueOptions.put(16, generateRowof(new ArrayList<>(Arrays.asList(5,2,3)), new ArrayList<>(Arrays.asList(Command.SHOW,Command.HITT,Command.SRND))));
        valueOptions.put(15, generateRowof(new ArrayList<>(Arrays.asList(5,3,1,1)), new ArrayList<>(Arrays.asList(Command.SHOW, Command.HITT, Command.SRND, Command.HITT))));
        valueOptions.put(14, generateRowof(new ArrayList<>(Arrays.asList(5,5)), new ArrayList<>(Arrays.asList(Command.SHOW, Command.HITT))));
        valueOptions.put(13, valueOptions.get(14));
        valueOptions.put(12, generateRowof(new ArrayList<>(Arrays.asList(2,3,5)), new ArrayList<>(Arrays.asList(Command.HITT, Command.SHOW, Command.HITT))));
        valueOptions.put(11, generateRowof(new ArrayList<>(Arrays.asList(10)), new ArrayList<>(Arrays.asList(Command.BETT))));
        valueOptions.put(10, generateRowof(new ArrayList<>(Arrays.asList(8,2)), new ArrayList<>(Arrays.asList(Command.BETT, Command.HITT))));
        valueOptions.put(9, generateRowof(new ArrayList<>(Arrays.asList(1,4,5)), new ArrayList<>(Arrays.asList(Command.HITT, Command.BETT, Command.HITT))));
        valueOptions.put(8, generateRowof(new ArrayList<>(Arrays.asList(10)), new ArrayList<>(Arrays.asList(Command.HITT))));
        valueOptions.put(7, valueOptions.get(8));
        valueOptions.put(6, valueOptions.get(8));
        valueOptions.put(5, valueOptions.get(8));
        valueOptions.put(4, valueOptions.get(8));



    }

    private void fillSoftOptions(){
        // Fill all the soft options that can be taken
        softOptions.put(9, generateRowof(new ArrayList<>(Arrays.asList(10)), new ArrayList<>(Arrays.asList(Command.SHOW))));
        softOptions.put(8, generateRowof(new ArrayList<>(Arrays.asList(4,1,5)), new ArrayList<>(Arrays.asList(Command.SHOW,Command.BETT, Command.SHOW))));
        softOptions.put(7, generateRowof(new ArrayList<>(Arrays.asList(5,2,3)), new ArrayList<>(Arrays.asList(Command.BETT, Command.SHOW, Command.HITT))));
        softOptions.put(6, generateRowof(new ArrayList<>(Arrays.asList(1,4,5)), new ArrayList<>(Arrays.asList(Command.HITT, Command.BETT, Command.HITT))));
        softOptions.put(5, generateRowof(new ArrayList<>(Arrays.asList(2,3,5)), new ArrayList<>(Arrays.asList(Command.HITT, Command.BETT, Command.HITT))));
        softOptions.put(4, softOptions.get(5));
        softOptions.put(3, generateRowof(new ArrayList<>(Arrays.asList(3,2,5)), new ArrayList<>(Arrays.asList(Command.HITT, Command.BETT, Command.HITT))));
        softOptions.put(2, softOptions.get(3));
    }


    /**
     * Returns an ArrayList containing N amount of commands of the same type
     * @param size amount of Commands to created
     * @param cmd Command to create
     * @return ArrayList<Command> N commands
     */
    private ArrayList<Command> generateNCommands(int size, Command cmd){
        ArrayList<Command> row = new ArrayList<>();
        for(int i = 0; i < size; i++){
            row.add(cmd);
        }
        return row;
    }

    /**
     * Generate a row from the amount of each command and its target command
     * @param amounts Size for each command
     * @param cmds Commands that are the target to be generated
     * @return ArrayList<Command> row from the table of WikiPedia
     */
    private ArrayList<Command> generateRowof(ArrayList<Integer> amounts, ArrayList<Command> cmds){
        ArrayList<Command> row = new ArrayList<>();

        for(int i = 0; i < amounts.size(); i++){
            int amount = amounts.get(i);
            Command cmd = cmds.get(i);
            row.addAll(generateNCommands(amount, cmd));
        }
        return row;
    }

    /**
     * Checks if is the end of the game
     * @return endOfGame
     */
    public boolean isEndOfGame() {
        return endOfGame;
    }

    /**
     * Set the game to its start or ending
     * @param endOfGame start or end
     */
    public void setEndOfGame(boolean endOfGame) {
        this.endOfGame = endOfGame;
    }
}
