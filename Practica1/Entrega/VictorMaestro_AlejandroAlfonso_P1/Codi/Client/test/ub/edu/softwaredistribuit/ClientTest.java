package ub.edu.softwaredistribuit;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ub.edu.softwaredistribuit.commons.*;
import ub.edu.softwaredistribuit.commons.players.Dealer;
import ub.edu.softwaredistribuit.commons.players.Player;
import ub.edu.softwaredistribuit.exceptions.*;
import ub.edu.softwaredistribuit.utils.ComUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private ComUtils server;
    private ComUtils client;
    private File sf;
    private File cf;

    private Player playerSV;
    private Dealer dealerSV;

    private Player playerCL;
    private Dealer dealerCL;

    private int PLAYER_ID = 0;
    private int INITIAL_BET = 100;

    private int currentBet = 0;


    @BeforeEach
    void init() {

        sf = new File("TestServer.txt");
        cf = new File("TestClient.txt");

        try {
            sf.createNewFile();
            cf.createNewFile();
            client = new ComUtils(new FileInputStream(sf), new FileOutputStream(cf));
            server = new ComUtils(new FileInputStream(cf), new FileOutputStream(sf));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void end(){
        if(sf.exists()) sf.delete();
        if(cf.exists()) cf.delete();
    }


    @Test
    void testStart() {
        assertDoesNotThrow(this::startAction);
    }

    @Test
    void testCash(){
        assertDoesNotThrow(this::startAction);
        assertDoesNotThrow(this::cashAction);
    }

    @Test
    void testBett(){
        assertDoesNotThrow(this::startAction);
        assertDoesNotThrow(this::cashAction);
        assertDoesNotThrow(this::betAction);
    }

    @Test
    void testHitt(){
        assertDoesNotThrow(this::startAction);
        assertDoesNotThrow(this::cashAction);
        assertDoesNotThrow(this::betAction);
        assertDoesNotThrow(this::hitAction);
    }

    @Test
    void testSurrender(){
        assertDoesNotThrow(this::startAction);
        assertDoesNotThrow(this::cashAction);
        assertDoesNotThrow(this::surrenderAction);
    }

    @Test
    void testShow(){
        assertDoesNotThrow(this::startAction);
        assertDoesNotThrow(this::cashAction);
        assertDoesNotThrow(this::betAction);
        assertDoesNotThrow(this::showAction);
    }

    @Test
    void testReplay(){
        assertDoesNotThrow(this::startAction);
        assertDoesNotThrow(this::cashAction);
        assertDoesNotThrow(this::betAction);
        assertDoesNotThrow(this::showAction);
        assertDoesNotThrow(this::replayAction);
    }

    @Test
    void testWrongActionCommand(){
        assertThrows(UnexpectedCommandException.class, this::wrongActionFromServer);
    }

    @Test
    void testUnknownActionCommand(){
        assertThrows(UnknownActionException.class, this::unknownActionFromServer);
    }


    void startAction() throws IOException, UnknownActionException {

        //CLIENT sends the Start
        client.write_command(Command.STRT);
        client.write_space();
        client.write_int32(PLAYER_ID);

        playerCL = new Player(PLAYER_ID);
        dealerCL = new Dealer();


        //SERVER reads the Start
        Command cmd = server.read_command();
        String space = server.read_space();
        int playerID = server.read_int32();


        playerSV = new Player(playerID);
        dealerSV = new Dealer();


        // SERVER responds with init
        server.write_command(Command.INIT);
        server.write_space();
        server.write_int32(INITIAL_BET);

        currentBet = INITIAL_BET;


        // Verify that we read INIT from the CLIENT
        assertEquals(client.read_command(), Command.INIT);
        assertEquals(client.read_space(), " ");
        assertEquals(client.read_int32(), INITIAL_BET);

    }

    void cashAction() throws IOException, UnknownActionException, NoExistSuitException, NoExistRankException {

        //CLIENT sends CASH
        client.write_command(Command.CASH);
        client.write_space();
        client.write_int32(INITIAL_BET + 800);

        //SERVER reads the cash
        Command cmd = server.read_command();
        String space = server.read_space();
        int cash = server.read_int32();
        playerSV.setBalance(cash);

        // Check if is bigger than the initial bet
        assertTrue(cash > INITIAL_BET);

        // Server answers with IDCK and SHOW
        server.write_command(Command.IDCK);
        playerSV.addCardToHand(new Card(Suit.HEARTS, Rank.ACE));
        playerSV.addCardToHand(new Card(Suit.CLUBS, Rank.ACE));
        for(int i = 0; i < 2; i++){
            server.writeCard(playerSV.getHand().get(i));
        }

        // Server adds also the SHOW one of his cards
        server.write_command(Command.SHOW);
        server.write_space();
        server.writeChar(Character.forDigit(1,10));
        dealerSV.addCardToHand(new Card(Suit.DIAMONDS, Rank.THREE));
        server.writeCard(dealerSV.getHand().get(0));

        // Read server Commands
        assertEquals(client.read_command(), Command.IDCK);
        for(int i = 0; i < 2; i++){
            playerCL.addCardToHand(client.readCard());
        }

        assertTrue(playerCL.getHand().equals(playerSV.getHand()));

        assertEquals(client.read_command(), Command.SHOW);
        assertEquals(client.read_space(), " ");
        assertEquals(client.readChar(), "1");
        dealerCL.addCardToHand(client.readCard());

        assertTrue(dealerCL.getHand().equals(dealerSV.getHand()));

    }

    void betAction() throws IOException, UnknownActionException {
        //Send BETT
        client.write_command(Command.BETT);

        //Server reads BETT
        Command cmd = server.read_command();
        assertTrue(playerSV.getBalance() >= currentBet*2);

    }

    void hitAction() throws IOException, UnknownActionException, NoExistSuitException, NoExistRankException {
        //Client send HITT
        client.write_command(Command.HITT);

        //Server read HITT and sends the new Card
        Command cmd = server.read_command();
        Card c = new Card(Suit.CLUBS, Rank.SIX);
        playerSV.addCardToHand(c);

        server.write_command(Command.CARD);
        server.writeCard(c);

        //Client reads the new Card
        assertEquals(Command.CARD, client.read_command());
        playerCL.addCardToHand(client.readCard());

        assertTrue(playerCL.getHand().equals(playerSV.getHand()));
    }

    void surrenderAction() throws IOException, UnknownActionException {
        client.write_command(Command.SRND);
        Command cmd = server.read_command();

        assertEquals(playerCL.hasBusted(), false);
    }

    void showAction() throws IOException, UnknownActionException, NoExistSuitException, NoExistRankException, NoExistWinnerException {
        //Send client hand
        client.write_command(Command.SHOW);
        client.write_space();
        client.writeChar(Character.forDigit(playerCL.getCardsAmount(), 10));
        for(int i = 0; i < playerCL.getCardsAmount(); i++){
            client.writeCard(playerCL.getHand().get(i));
        }

        //check player's hand is done in server's test
        //Send server hand

        while(dealerSV.getHandValue() < 17){
            dealerSV.addCardToHand(new Card(Suit.CLUBS, Rank.THREE));
        }

        server.write_command(Command.SHOW);
        server.write_space();
        server.writeChar(Character.forDigit(dealerSV.getCardsAmount(), 10));
        for(int i = 1; i < dealerSV.getCardsAmount(); i++){
            server.writeCard(dealerSV.getHand().get(i));
        }

        server.writeWinner(Winner.SERVER, currentBet);

        //Now test if we read correctly winner and show
        assertEquals(client.read_command(), Command.SHOW);
        assertEquals(client.read_space(), " ");
        int cards = Integer.parseInt(client.readChar());
        for(int i = 0; i < cards-1; i++){
            Card c = client.readCard();
            dealerCL.addCardToHand(c);
        }

        assertTrue(dealerCL.getHand().equals(dealerSV.getHand()));

        assertEquals(client.read_command(), Command.WINS);
        Pair<Winner,Integer> wins = client.readWinner();
        assertEquals(wins.getKey(), Winner.SERVER);
        assertEquals(wins.getValue(), currentBet);

    }

    void replayAction() throws IOException, UnknownActionException {

        client.write_command(Command.RPLY);

        // SERVER responds with init
        server.write_command(Command.INIT);
        server.write_space();
        server.write_int32(INITIAL_BET);

        currentBet = INITIAL_BET;

        // Verify that we read INIT from the CLIENT
        assertEquals(client.read_command(), Command.INIT);
        assertEquals(client.read_space(), " ");
        assertEquals(client.read_int32(), INITIAL_BET);

    }

    void wrongActionFromServer() throws IOException, UnknownActionException, UnexpectedCommandException {
        //CLIENT sends the Start
        client.write_command(Command.STRT);
        client.write_space();
        client.write_int32(PLAYER_ID);

        server.write_command2("CASH");
        server.write_space();
        server.write_int32(1000);

        //We expect INIT but we recive CASH
        Command cmd = client.read_command();
        if(cmd != Command.INIT){
            throw new UnexpectedCommandException(cmd.name());
        }

    }

    void unknownActionFromServer() throws IOException, UnknownActionException {
        //CLIENT sends the Start
        client.write_command(Command.STRT);
        client.write_space();
        client.write_int32(PLAYER_ID);

        server.write_command2("PEPE");
        server.write_space();
        server.write_int32(1000);

        //We expect INIT but we recive PEPE which is not known
        Command cmd = client.read_command();

    }


}