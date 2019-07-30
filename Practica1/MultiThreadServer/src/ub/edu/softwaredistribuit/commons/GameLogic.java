package ub.edu.softwaredistribuit.commons;

import ub.edu.softwaredistribuit.commons.players.Dealer;
import ub.edu.softwaredistribuit.commons.players.GamePlayer;
import ub.edu.softwaredistribuit.commons.players.Player;
import ub.edu.softwaredistribuit.exceptions.UnknownActionException;
import ub.edu.softwaredistribuit.exceptions.WrongActionException;
import ub.edu.softwaredistribuit.exceptions.MaxCashExceedException;
import ub.edu.softwaredistribuit.exceptions.NotEnoughCoinsException;
import ub.edu.softwaredistribuit.exceptions.ServerSideErrorException;
import ub.edu.softwaredistribuit.exceptions.NotEnoughCashException;
import ub.edu.softwaredistribuit.exceptions.NotClientCommandException;
import ub.edu.softwaredistribuit.exceptions.NoExistSuitException;
import ub.edu.softwaredistribuit.exceptions.NoExistRankException;
import ub.edu.softwaredistribuit.exceptions.PlayerCheatsException;
import ub.edu.softwaredistribuit.utils.ComUtils;
import ub.edu.softwaredistribuit.utils.LogUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Game server logic
 */
public class GameLogic implements ILogic {

    private ComUtils comUtils;
    private LogUtils logUtils;

    //Comunication Socket
    private Socket socket;

    // Game stuff
    private final int INITIAL_BET = 100;
    private Deck deck;
    private Player client;
    private Dealer dealer;

    private int currentBet;

    private GameState gameState;


    /**
     * Create a new GameLogic
     * @param socket Socket
     * @throws IOException IOException Error
     */
    public GameLogic(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(500 * 1000);
        comUtils = new ComUtils(socket);
        this.gameState = GameState.WAITING;
    }

    /**
     * Get the socket used in communication
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Set the logUtils
     * @param logUtils LogUtils class
     */
    public void setLogUtils(LogUtils logUtils) {
        this.logUtils = logUtils;
    }

    /**
     * Game loop logic. Starts the game
     * @throws WrongActionException WrongActionException Error
     * @throws IOException IOException Error
     * @throws UnknownActionException UnknownActionException Error
     * @throws MaxCashExceedException MaxCashExceedException Error
     * @throws NotEnoughCoinsException NotEnoughCoinsException Error
     * @throws ServerSideErrorException ServerSideErrorException Error
     * @throws NotEnoughCashException NotEnoughCashException Error
     * @throws NotClientCommandException NotClientCommandException Error
     * @throws NoExistSuitException NoExistSuitException Error
     * @throws NoExistRankException NoExistRankException Error
     * @throws PlayerCheatsException PlayerCheatsException Error
     */
    public void start() throws WrongActionException, IOException, UnknownActionException,
            MaxCashExceedException, NotEnoughCoinsException, ServerSideErrorException,
            NotEnoughCashException, NotClientCommandException, NoExistSuitException, NoExistRankException, PlayerCheatsException {
        Command cmd = null;

        do {
            // Read the command from the server
            cmd = this.comUtils.read_command();

            System.out.println("Command recived: " + cmd.name());

            switch (cmd) {
                case STRT:
                    this.startGame(cmd);
                    break;
                case CASH:
                    this.checkCash(cmd);
                    break;
                case HITT:
                    this.askForCard(cmd);
                    break;
                case SHOW:
                    this.changeTurn();
                    break;
                case BETT:
                    bett(cmd);
                    break;
                case SRND:
                    surrender(cmd);
                    break;
                case RPLY:
                    replay(cmd);
                    break;
                case ERRO:
                    this.errorFromClient();
                    break;
                case EXIT:
                    this.logClient(cmd.name());
                    break;

                default:
                    notClientCmd(cmd);
                    break;
            }

        } while (cmd != Command.EXIT);
    }

    /**
     * Read N cards from the Client side
     * @param n Number of cards to read
     * @return Arraylist with received hand
     * @throws IOException IOException Error
     * @throws NoExistRankException NoExistRankException Error
     * @throws NoExistSuitException NoExistSuitException Error
     */
    public ArrayList<Card> readNCards(int n) throws IOException, NoExistRankException, NoExistSuitException {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cards.add(comUtils.readCard());
        }
        return cards;

    }

    /**
     * Change the turn from Client to Server
     * @throws IOException IOException
     * @throws NoExistSuitException NoExistSuitException Error
     * @throws NoExistRankException NoExistRankException Error
     * @throws PlayerCheatsException PlayerCheatsException Error
     */
    @Override
    public void changeTurn() throws IOException, NoExistSuitException, NoExistRankException, PlayerCheatsException {


        if (gameState == GameState.JUST_SURRENDER){
            this.gameState = GameState.DEALER_TURN;
            // We have just surrendered so this is a TIE
            this.tieWins();

        }else if (gameState == GameState.SHOW_1C_DEALER || gameState == GameState.GIVING_CARD || gameState == GameState.JUST_BETT) {
            comUtils.read_space();
            int len = Integer.parseInt(comUtils.readChar()); // Number of cards to read
            // We now read the N cards from the server
            ArrayList<Card> receivedCards = readNCards(len);

            System.out.print("\n");
            boolean cheats = dealer.checkPlayerHand(receivedCards, client);

            // Check if player is cheating on us
            if(cheats){
                comUtils.writeError("ERROR: The received cards are not equal to the server data!");
                throw new PlayerCheatsException();
            }

            // Log the client hand
            StringBuilder toLog = new StringBuilder(Command.SHOW.name() + " " + len);
            for(Card c : receivedCards){
                toLog.append(" ").append(c.toString());
            }

            this.logClient(toLog.toString());



            this.gameState = GameState.DEALER_TURN;
            if (client.hasBusted()) { // Server wins directly
                this.serverWins();
            } else {
                if (client.hasBlackJax() && dealer.hasBlackJax()) {
                    //Update State
                    this.sendCurrentHand(dealer);
                    // This is a TIE
                    this.tieWins();
                } else {
                    // If the player hasnt busted <= 21 and dealer hasnt at least 17 points then get a card til we have it
                    while (dealer.getHandValue() < 17) {
                        dealer.dealNewCardTo(deck, dealer);
                    }

                    this.sendCurrentHand(dealer); // Show the hand to the player

                    //Get who wins
                    if (dealer.hasBusted() || (client.getHandValue() > dealer.getHandValue()) || client.hasBlackJax() && dealer.has21()) {
                        this.clientWins();
                    } else if(client.has21() && dealer.hasBlackJax() || client.getHandValue() < dealer.getHandValue()){
                        this.serverWins();
                    }else{
                        this.tieWins();
                    }
                }

            }
        }
        //Change game state
        gameState = GameState.WIN;
    }

    /**
     * Send a TIE to the Client
     * @throws IOException IOException Error
     */
    @Override
    public void tieWins() throws IOException {
        // Send who wins as TIE
        comUtils.writeWinner(Winner.TIE, currentBet);
        //Log the send command
        logServer(Command.WINS.name() + " " + Winner.TIE.ordinal() + " " + currentBet);
        this.currentBet = INITIAL_BET;
    }

    /**
     * Send a SERVER wins to the Client
     * @throws IOException IOException Error
     */
    @Override
    public void serverWins() throws IOException {
        comUtils.writeWinner(Winner.SERVER, currentBet);
        //Log the send command
        logServer(Command.WINS.name() + " " + Winner.SERVER.ordinal() + " " + currentBet);
        // Update the balance
        this.client.substract(currentBet);
    }

    /**
     * Send a CLIENT wins to the client
     * @throws IOException IOException Error
     */
    @Override
    public void clientWins() throws IOException {
        comUtils.writeWinner(Winner.CLIENT, currentBet);
        //Log the send command
        logServer(Command.WINS.name() + " " + Winner.CLIENT.ordinal() + " " + currentBet);
        // Update the balance
        this.client.add(currentBet);
    }

    /**
     * Send the current GamePlayer's Hand to the Client as a SHOW
     * @param gamePlayer GamePlayer
     * @throws IOException IOException Error
     */
    @Override
    public void sendCurrentHand(GamePlayer gamePlayer) throws IOException {
        //Send show command with parameters
        comUtils.write_command(Command.SHOW);
        comUtils.write_space();
        comUtils.writeChar((char) Character.forDigit(gamePlayer.getCardsAmount(), 10));

        // Send the cards and create the string for the log
        StringBuilder logString = new StringBuilder();
        for(Card c : gamePlayer.getHand()){
            comUtils.writeCard(c);
            logString.append(" ").append(c.getCharRank()).append(c.getSuit().getUnicode());
        }

        logServer(Command.SHOW + " " + gamePlayer.getCardsAmount() + logString);

    }


    /**
     * Read an error incoming from the Client
     * @throws IOException IOException Error
     */
    @Override
    public void errorFromClient() throws IOException {
        this.gameState = GameState.ERROR_CLIENT;

        String error = comUtils.readError();
        this.logClient(Command.ERRO.name() + error);
        this.closeConnection();
        System.out.print("Connection Closed!");
    }

    /**
     * Send an error to the client for a command received from him
     * @param cmd Command received
     * @throws IOException IOException Error
     * @throws NotClientCommandException NotClientCommandException Error
     */
    @Override
    public void notClientCmd(Command cmd) throws IOException, NotClientCommandException {
        comUtils.writeError("ERROR: The taken action is incorrect!");
        throw new NotClientCommandException(cmd.name());
    }

    /**
     * Called at the start of the game when we receive the STRT
     * @param cmd Command received
     * @throws WrongActionException WrongActionException Error
     * @throws IOException IOException Error
     * @throws ServerSideErrorException ServerSideErrorException Error
     * @throws NotEnoughCoinsException NotEnoughCoinsException Error
     */
    @Override
    public void startGame(Command cmd) throws WrongActionException, IOException, ServerSideErrorException, NotEnoughCoinsException {

        if (this.gameState == GameState.WAITING) {

            String sp = comUtils.read_space();
            int playerID = comUtils.read_int32();

            // Log the Client msg
            logClient(cmd.name() + sp + playerID);

            // Init game stuff
            this.client = new Player(playerID);
            this.dealer = new Dealer();
            this.deck = new Deck();

            System.out.println("New game created with playerID " + playerID);

            // Resend the INIT to the Client - Server will now wait for a CASH cmd
            this.sendInit();

        } else {
            wrongActionError(cmd); // Send WrongActionError and throw it!
        }

    }

    /**
     * Called at the start of the game when server needs to send a response as INIT
     * @throws IOException IOException Error
     * @throws ServerSideErrorException ServerSideErrorException Error
     * @throws NotEnoughCoinsException NotEnoughCoinsException Error
     */
    @Override
    public void sendInit() throws IOException, ServerSideErrorException, NotEnoughCoinsException {

        if (gameState == GameState.WAITING || gameState == GameState.REPLAYING) {

            // If Player is replaying check his current balance
            // to see if he has enough money to play a new game
            if (this.client.getBalance() < INITIAL_BET && gameState == GameState.REPLAYING) {
                comUtils.writeError("ERROR: Not enough coins to start a new Game!");
                throw new NotEnoughCoinsException();
            }

            //Update GameState and currentBet for a new Game
            this.gameState = GameState.CREATING;
            this.currentBet = INITIAL_BET;


            comUtils.write_command(Command.INIT);
            comUtils.write_space();
            comUtils.write_int32(INITIAL_BET);

            this.logServer(Command.INIT.name() + " " + INITIAL_BET);

        } else {
            comUtils.writeError("Error: There was an error in the server state tracker!");
            throw new ServerSideErrorException();
        }
    }

    /**
     * Called when player wants to surrend
     * @param cmd Command Received
     * @throws IOException IOException Error
     * @throws WrongActionException WrongActionException Error
     * @throws NoExistRankException NoExistRankException Error
     * @throws NoExistSuitException NoExistSuitException Error
     * @throws PlayerCheatsException PlayerCheatsException Error
     */
    @Override
    public void surrender(Command cmd) throws IOException, WrongActionException, NoExistRankException, NoExistSuitException, PlayerCheatsException {

        //TODO: Not necessary! Check if the client is not cheating us! surrending with more than 21!
        if (gameState == GameState.SHOW_1C_DEALER || gameState == GameState.GIVING_CARD) {

            //Update the state
            gameState = GameState.JUST_SURRENDER;
            //Log the command
            logClient(cmd.name());

            this.changeTurn();

        } else {
            wrongActionError(cmd); // Send WrongActionError and throw it!
        }
    }

    /**
     * Called when the player wants to do a replay
     * @param cmd Command Received
     * @throws IOException IOException Error
     * @throws WrongActionException WrongActionException Error
     * @throws ServerSideErrorException ServerSideErrorException Error
     * @throws NotEnoughCoinsException NotEnoughCoinsException Error
     */
    @Override
    public void replay(Command cmd) throws IOException, WrongActionException, ServerSideErrorException, NotEnoughCoinsException {
        if (gameState == GameState.WIN) {

            //Update Game state
            this.gameState = GameState.REPLAYING;
            //Log client
            logClient(cmd.name());

            //Restart the game
            this.deck = new Deck(); //Get a new deck for a new game
            this.dealer.clearHand();
            this.client.clearHand();

            //Send the init to the client
            this.sendInit();

        } else {
            wrongActionError(cmd); // Send WrongActionError and throw it!
        }
    }

    /**
     * Called when Client does a Cash
     * @param cmd Command Received
     * @throws WrongActionException WrongActionException Error
     * @throws IOException IOException Error
     * @throws NotEnoughCashException NotEnoughCashException Error
     */
    @Override
    public void checkCash(Command cmd) throws WrongActionException, IOException, NotEnoughCashException {
        if (this.gameState == GameState.CREATING) {

            String sp = comUtils.read_space();
            int cash = comUtils.read_int32();

            // Log the Client msg
            logClient(cmd.name() + sp + cash);

            //Set client balance
            this.client.setBalance(cash);

            if (cash < INITIAL_BET) {
                comUtils.writeError("ERROR: The MaxCash should be bigger than the initial bet");
                throw new NotEnoughCashException();
            }

            this.sendInitialHand();

            // Show one dealer
            this.showOneDealerCard();

        } else {
            wrongActionError(cmd); // Send WrongActionError and throw it!
        }
    }

    /**
     * Send the initial hand for the Client and deal the cards for each players
     * @throws IOException IOException Error
     */
    @Override
    public void sendInitialHand() throws IOException {
        this.gameState = GameState.INITIAL_HAND;
        // Deal the cards to the player and himself (dealer)
        dealer.dealCards(deck, client);

        // Send the hand for the player
        this.sendHand(this.client);

    }

    /**
     * Send one dealer's card to the Client
     * @throws IOException IOException Error
     */
    @Override
    public void showOneDealerCard() throws IOException {
        this.gameState = GameState.SHOW_1C_DEALER;
        comUtils.write_command(Command.SHOW);
        comUtils.write_space();
        comUtils.writeChar(Character.forDigit(1, 10));

        Card c = dealer.getHand().get(0);
        //We write a space inside writecard
        comUtils.writeCard(dealer.getHand().get(0));

        logServer(Command.SHOW + " " + '1' + " " + c.getCharRank() + c.getSuit().getUnicode());

    }

    /**
     * Send the initial hand to the Client
     * @param gamePlayer GamePlayer client
     * @throws IOException IOException Error
     */
    @Override
    public void sendHand(GamePlayer gamePlayer) throws IOException {
        comUtils.write_command(Command.IDCK);
        StringBuilder logString = new StringBuilder();
        for (Card c : gamePlayer.getHand()) {
            comUtils.writeCard(c);
            logString.append(" ").append(c.getCharRank()).append(c.getSuit().getUnicode());
        }

        logServer(Command.IDCK.name() + logString.toString());
    }

    /**
     * Called when player asks for a new card
     * @param cmd Command Received
     * @throws WrongActionException WrongActionException Error
     * @throws IOException IOException Error
     */
    @Override
    public void askForCard(Command cmd) throws WrongActionException, IOException {
        if (gameState == GameState.SHOW_1C_DEALER || gameState == GameState.JUST_BETT || gameState == GameState.GIVING_CARD) {
            //TODO: Not necessary! Check if player has more than 21 if that throw an error!
            logClient(cmd.name());
            //Give a new card to the client hand
            Card c = dealer.dealNewCardTo(deck, client);

            // Send the new Card to the client
            comUtils.write_command(Command.CARD);
            comUtils.write_space();
            comUtils.writeChar(c.getCharRank());
            comUtils.writeSuit(c.getSuit());

            //Log
            logServer(Command.CARD.name() + " " + c.getCharRank() + c.getSuit().getUnicode());

            gameState = GameState.GIVING_CARD;


        } else {
            wrongActionError(cmd); // Send WrongActionError and throw it!
        }
    }


    /**
     * Called when Client does a Bet
     * @param cmd Command Received
     * @throws WrongActionException WrongActionException Error
     * @throws IOException IOException Error
     * @throws MaxCashExceedException MaxCashExceedException Error
     */
    @Override
    public void bett(Command cmd) throws WrongActionException, IOException, MaxCashExceedException {
        // Check if we are in the correct game State
        if (gameState == GameState.GIVING_CARD || gameState == GameState.SHOW_1C_DEALER) {

            gameState = GameState.JUST_BETT;

            // check if player can bet
            if (client.canBett(this.currentBet)){
                this.currentBet *= 2;
                logClient(cmd.name());
            } else {
                comUtils.writeError("Error: Exceeding the maximum cash for this game!");
                throw new MaxCashExceedException();
            }

        } else {
            wrongActionError(cmd); // Send WrongActionError and throw it!
        }
    }

    /**
     * Close the connection with the client
     */
    @Override
    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log to the File as Client Info
     * @param log Log String
     */
    @Override
    public void logClient(String log) {
        logUtils.info("C: " + log);
    }

    /**
     * Log to the File as Server Info
     * @param log Log String
     */
    @Override
    public void logServer(String log) {
        logUtils.info("S: " + log);
    }

    /**
     * Send an error to the client for incorrect action taken by him
     * @param cmd Command Received
     * @throws IOException IOException Error
     * @throws WrongActionException WrongActionException Error
     */
    @Override
    public void wrongActionError(Command cmd) throws IOException, WrongActionException {
        comUtils.writeError("ERROR: The taken action [ " + cmd.name() + " ] is incorrect for the current GameState");
        throw new WrongActionException(cmd.name());
    }

}
