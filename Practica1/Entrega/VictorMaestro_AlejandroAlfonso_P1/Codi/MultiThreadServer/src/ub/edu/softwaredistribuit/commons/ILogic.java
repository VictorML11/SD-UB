package ub.edu.softwaredistribuit.commons;

import ub.edu.softwaredistribuit.commons.players.GamePlayer;
import ub.edu.softwaredistribuit.exceptions.*;

import java.io.IOException;

/**
 * Game Server Logic methods
 */
public interface ILogic {

    //////////
    // STRT //
    //////////

    void startGame(Command cmd) throws WrongActionException, IOException, ServerSideErrorException, NotEnoughCoinsException;
    void sendInit() throws IOException, ServerSideErrorException, NotEnoughCoinsException;

    //////////
    // CASH //
    //////////

    void checkCash(Command cmd) throws WrongActionException, IOException, NotEnoughCashException;
    void sendInitialHand() throws IOException;
    void showOneDealerCard() throws IOException;

    //////////
    // IDCK //
    //////////

    void sendHand(GamePlayer gamePlayer) throws IOException;

    //////////
    // HIT  //
    //////////

    void askForCard(Command cmd) throws WrongActionException, IOException;
    void sendCurrentHand(GamePlayer gamePlayer) throws IOException;

    //////////
    // SRND //
    //////////

    void surrender(Command cmd) throws IOException, WrongActionException, NoExistRankException, NoExistSuitException, PlayerCheatsException;

    //////////
    // RPLY //
    //////////

    void replay(Command cmd) throws IOException, WrongActionException, ServerSideErrorException, NotEnoughCoinsException;

    //////////
    // BETT //
    //////////

    void bett(Command cmd) throws WrongActionException, IOException, MaxCashExceedException;

    //////////
    // WINS //
    //////////

    void changeTurn() throws IOException, NoExistSuitException, NoExistRankException, PlayerCheatsException;
    void serverWins() throws IOException;
    void tieWins() throws IOException;
    void clientWins() throws IOException;

    ///////////
    // OTHER //
    ///////////

    void closeConnection();

    void logClient(String log);
    void logServer(String log);

    void wrongActionError(Command cmd) throws IOException, WrongActionException;
    void notClientCmd(Command cmd) throws IOException, NotClientCommandException;
    void errorFromClient() throws IOException;

}
