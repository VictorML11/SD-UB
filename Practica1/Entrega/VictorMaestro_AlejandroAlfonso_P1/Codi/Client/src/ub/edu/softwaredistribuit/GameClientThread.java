package ub.edu.softwaredistribuit;

import javafx.util.Pair;
import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.Command;
import ub.edu.softwaredistribuit.commons.GameMode;
import ub.edu.softwaredistribuit.commons.players.Dealer;
import ub.edu.softwaredistribuit.commons.players.GamePlayer;
import ub.edu.softwaredistribuit.commons.players.Player;
import ub.edu.softwaredistribuit.commons.Winner;
import ub.edu.softwaredistribuit.exceptions.NoExistRankException;
import ub.edu.softwaredistribuit.exceptions.NoExistSuitException;
import ub.edu.softwaredistribuit.exceptions.NoExistWinnerException;
import ub.edu.softwaredistribuit.exceptions.UnknownActionException;
import ub.edu.softwaredistribuit.exceptions.UnexpectedCommandException;
import ub.edu.softwaredistribuit.exceptions.ServerSideErrorException;
import ub.edu.softwaredistribuit.utils.BasicStrategy;
import ub.edu.softwaredistribuit.utils.ComUtils;
import ub.edu.softwaredistribuit.utils.CustomMenu;
import ub.edu.softwaredistribuit.utils.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * GameClient Logic class
 */
public class GameClientThread implements Runnable {

    // Communication stuff
    private ComUtils comUtils;
    private Socket socket;

    // Game information
    private CustomMenu menu = new CustomMenu("Game Command Menu");
    private Player player;
    private Dealer dealer;
    private int initialBet;

    // Game Strategy
    private GameMode gamemode;

    private boolean automatic;
    private boolean basicStrategy;
    private boolean manual;

    private BasicStrategy bs;


    /**
     * Create a new GameClientThread
     * @param socket Socket connection with the server
     * @param gamemode Game mode to use in this game
     * @throws IOException IOException Error
     */
    public GameClientThread(Socket socket, GameMode gamemode) throws IOException {
        comUtils = new ComUtils(socket);
        this.gamemode = gamemode;
        this.socket = socket;
        this.automatic = false;
        this.basicStrategy = false;
        this.manual = false;
    }


    /**
     * Starts the game Client Thread
     */
    @Override
    public void run() {
        System.out.println("\n===== Gamemode Stablished as " + gamemode.name() + " ======\n");

        // Check in which game mode we are going to be playing
        switch (gamemode) {
            case MANUAL:
                this.manual = true;
                break;
            case AUTOMATIC:
                this.automatic = true;
                break;
            case BASIC_STRAT:
                this.basicStrategy = true;
                this.bs = new BasicStrategy(menu);
                break;
        }

        Scanner sc = new Scanner(System.in);

        try {
            commandMenu(sc); //Start the game loop
        } catch (IOException | UnknownActionException | NoExistRankException
                | NoExistSuitException | NoExistWinnerException | UnexpectedCommandException
                | ServerSideErrorException e) {

            System.out.println("\n" + e.getMessage());
            e.printStackTrace();

        }finally {
            //Close Connection
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void commandMenu(Scanner sc) throws IOException, UnknownActionException, NoExistRankException, NoExistSuitException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {
        Command opcio = null;

        do {
            // Check first what game mode is to select a command
            if(manual){
                menu.showMenu();
                opcio = menu.getOption(sc);

            }else if(automatic){
                opcio = menu.selectRandomAction();
                System.out.println("Command Send: " + opcio.name());
            }else{
                // Basic strat
                opcio = this.bs.selectBestOption(player, dealer);
                System.out.println("Command Send: " + opcio.name());
            }

            // Send the Command to the server 4 Bytes
            comUtils.write_command(opcio);

            // Check what we have to do depending on the command sent
            switch (opcio) {

                case STRT:
                    startCommand(sc);
                    break;
                case CASH:
                    cashCommand(sc);
                    break;
                case HITT:
                    hittCommand();
                    break;
                case SHOW:
                    showCommand();
                    break;
                case BETT:
                    bettCommand();
                    break;
                case SRND:
                    surrenderCommand();
                    break;
                case RPLY:
                    replayCommand();
                    break;
            }

        } while (opcio != Command.EXIT);

    }

    private void readServerCommand(Command expectedCmd) throws IOException, UnknownActionException, NoExistRankException, NoExistSuitException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {

        //Read the command received from the server 4 Bytes
        // We already check if the cmd exists (not is null)
        Command cmd = comUtils.read_command();

        if(cmd == Command.ERRO || cmd == expectedCmd) { // If is one of our possible commands

            // Take an action depending on the received command
            switch (cmd) {
                case SHOW:
                    handleShow();
                    break;
                case INIT:
                    this.handleInit();
                    break;
                case IDCK:
                    this.handleIdck();
                    break;
                case CARD:
                    this.handleCard();
                    break;
                case WINS:
                    this.handleWins();
                    break;
                case ERRO:
                    this.errorFromServer();
                    break;
            }
        } else {
            // This means the server send us a command we didnt expect GameState is lost
            comUtils.writeError("ERROR: Unexpected received Command [ " + cmd.name() + " ]");
            throw new UnexpectedCommandException(cmd.name());
        }
    }

    private void errorFromServer() throws IOException, ServerSideErrorException {
        // We received an error from the server. Read the error and throw a new exception
        String error = comUtils.readError();
        throw new ServerSideErrorException(error);
    }

    private void startCommand(Scanner sc) throws IOException, UnknownActionException, NoExistSuitException, NoExistRankException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {
        int playerID;
        //Ask for the playerID to create a new Player and send to the server the command
        if(manual){

            do {
                playerID = Utils.askForNumber(sc, "Introduce the player ID <Positive_Number>: ");
            } while (playerID <= 0);

        }else{
            playerID = Utils.generateRandomNumber(1, 1000);
        }


        System.out.println("Your playerID is: " + playerID);
        this.player = new Player(playerID);

        // Send the rest of the arguments of the command of STRT
        comUtils.write_space();
        comUtils.write_int32(playerID);

        // Read the answer from the server we expect INIT <N>
        this.readServerCommand(Command.INIT);

        // END: We return to the Command Menu -> CASH

    }

    private void handleInit() throws IOException {
        //Read INIT arguments from the server
        comUtils.read_space();
        initialBet = comUtils.read_int32();
        System.out.println("The Initial bet was set to: " + initialBet);
        // Create the dealer
        dealer = new Dealer();

        // Update Command Menu
        menu.changeCommandState(Command.STRT, false);
        menu.changeCommandState(Command.CASH, true);

        // END: We return to the Command Menu -> CASH
    }


    private void cashCommand(Scanner sc) throws UnknownActionException, IOException, NoExistSuitException, NoExistRankException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {

        int balance;
        //Ask for the player's balance and send to the server the command
        if(manual){
            boolean bigger = false;
            do {

                balance = Utils.askForNumber(sc,  "Enter the money you will use in the match <Positive_Number>: ");

                if(balance < initialBet){
                    System.out.println("Balance should be bigger than the initial Bett: " + initialBet);
                } else {
                    bigger = true;
                }

            } while (balance <= 0 || !bigger);

        }else{
            balance = this.initialBet + Utils.generateRandomNumber(1, 2000);
        }

        System.out.println("Cash stablished to " + balance + "$ for this game!");

        player.setBalance(balance);

        // Send the rest of the arguments of the command of CASH
        comUtils.write_space();
        comUtils.write_int32(balance);

        // Read the answer from the server we expect IDCK <N>
        readServerCommand(Command.IDCK);

        // Update the Command Menu: At this point
        // we have already read the IDCK and the SHOW from the server
        menu.changeCommandState(Command.CASH, false);
        menu.changeCommandState(Command.HITT, true);
        menu.changeCommandState(Command.BETT, true);
        menu.changeCommandState(Command.SHOW, true);
        menu.changeCommandState(Command.SRND, true);

        // END: We return to the Command Menu

    }

    private void handleIdck() throws IOException, UnknownActionException, NoExistSuitException, NoExistRankException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {
        // We first read the two initial cards of the player
        readNCard(this.player, 2, true, true);

        //Now we expect the One Card Show from the server
        readServerCommand(Command.SHOW);

        //Show the player's Hand
        player.showHand();

        // END: We return to the Command Menu
    }

    private void handleShow() throws IOException, NoExistSuitException, NoExistRankException {
        // Read the arguments from SHOW command
        comUtils.read_space();
        int len = Integer.parseInt(comUtils.readChar()); // Number of cards to read

        // Clear the dealer's hand we already to have to avoid duplicates
        dealer.clearHand();

        // We now read the N cards from the server
        readNCard(this.dealer, len, true, true);
        System.out.print("\n");

        //Show the dealer's Hand
        dealer.showHand();
    }

    private void hittCommand() throws UnknownActionException, IOException, NoExistSuitException, NoExistRankException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {

        //Now we expect Our Card from the server
        readServerCommand(Command.CARD);

        //At this point we return to the command menu we have already read the card

    }

    private void handleCard() throws IOException, NoExistSuitException, NoExistRankException {
        // We read 1 Card and add it to the player's hand and we show the hand
        readNCard(this.player, 1, true, true);
        player.showHand();

        //Update command menu if busted!
        if(player.hasBusted()){
            System.out.println("\nYou have just BUSTED! with: " + player.getHandValue());
            menu.changeCommandState(Command.SRND, false);
            menu.changeCommandState(Command.BETT, false);
            menu.changeCommandState(Command.HITT, false);
        }else{
            menu.changeCommandState(Command.BETT, true);
            menu.changeCommandState(Command.SRND, true);
        }

        // At this point we can return to the menu
    }


    private void showCommand() throws IOException, NoExistRankException, UnknownActionException, NoExistSuitException, NoExistWinnerException, UnexpectedCommandException, ServerSideErrorException {
        // With this command player Stands sending a show to the server
        Utils.sendCurrentHand(this.player, this.comUtils);

        // If player hasn't busted we expect first the dealer's hand
        if(!player.hasBusted()){
            readServerCommand(Command.SHOW);
        }

        //If has or not busted busted we expect wins in any case
        readServerCommand(Command.WINS);

        // At this point we can return to the menu we already update the commands
    }

    private void handleWins() throws IOException, NoExistWinnerException {
        // Get who wins from the command received of the server
        Pair<Winner, Integer> whoWins = comUtils.readWinner();
        Winner w = whoWins.getKey();
        int worth = whoWins.getValue();

        //Show the results
        if(w == Winner.CLIENT || w == Winner.SERVER){
            System.out.println("The game was won by: " + w.name().toUpperCase());
            System.out.println((w == Winner.CLIENT) ? "You made a profit of: " + worth :
                    "You lost a total of: " + worth);
        }else{
            System.out.println("The game ended as a " + w.name().toUpperCase());
            System.out.println("No-one lost: " + worth);
        }

        //Disable Available commands
        menu.changeCommandState(Command.SRND, false);
        menu.changeCommandState(Command.BETT, false);
        menu.changeCommandState(Command.HITT, false);
        menu.changeCommandState(Command.SHOW, false);


        // Enable EXIT or REPLAY in any case
        menu.changeCommandState(Command.RPLY, true);
        menu.changeCommandState(Command.EXIT, true);

        // Make sure we set the end of the game
        if(basicStrategy){
            bs.setEndOfGame(true);
        }

        // Return back

    }

    private void bettCommand(){
        // Update the menu only
        menu.changeCommandState(Command.SRND, false);
        menu.changeCommandState(Command.BETT, false); // Disable himself
    }

    private void surrenderCommand() throws NoExistWinnerException, UnknownActionException, NoExistSuitException, NoExistRankException, IOException, UnexpectedCommandException, ServerSideErrorException {
        // We expect a WIN from the server as a TIE
        System.out.println("Your have just Surrendered!");
        readServerCommand(Command.WINS);

        // At this point we can return to the menu we already update the commands
    }


    private void replayCommand() throws NoExistWinnerException, UnknownActionException, NoExistSuitException, NoExistRankException, IOException, UnexpectedCommandException, ServerSideErrorException {
        //Update game state as a start
        if(basicStrategy){
            this.bs.setEndOfGame(false);
        }

        // We expect INIT from the server or an Error
        readServerCommand(Command.INIT);

        //Update game stuff
        player.clearHand();
        dealer.clearHand();

        //Return to game Menu

        // Make sure to update menu options
        menu.changeCommandState(Command.RPLY, false);
        menu.changeCommandState(Command.EXIT, false);

    }


    /**
     * Read one card received from the server
     * @param p Player from we want to handle the cards
     * @param add Check if the read card should be added to the hand
     * @return Card read from the server
     * @throws IOException IOException Error
     * @throws NoExistRankException NoExistRankException Error
     * @throws NoExistSuitException NoExistSuitException Error
     */
    public Card readCard(GamePlayer p, boolean add) throws NoExistRankException, IOException, NoExistSuitException {
        Card c = comUtils.readCard();
        if (add) p.addCardToHand(c);
        return c;
    }


    /**
     *
     * @param p Player from we want to handle the cards
     * @param n Number of cards to read
     * @param add Check if the read card should be added to the hand
     * @param print Check if the read card should be shown to the player
     * @throws IOException IOException Error
     * @throws NoExistRankException NoExistRankException Error
     * @throws NoExistSuitException NoExistSuitException Error
     */
    public void readNCard(GamePlayer p, int n, boolean add, boolean print) throws NoExistRankException, IOException, NoExistSuitException {

        if(print){
            if(p instanceof Dealer){
                System.out.print("\nCurrent Dealer's Hand: ");
            }else if(p instanceof Player){
                System.out.print("\nReceived new Card(s): ");
            }
        }

        for (int i = 0; i < n; i++) {
            Card c = readCard(p, add);
            if (print) System.out.print(" " + c.toString());
        }

    }

}
