package ub.edu.softwaredistribuit;

import ub.edu.softwaredistribuit.commons.GameLogic;
import ub.edu.softwaredistribuit.exceptions.*;
import ub.edu.softwaredistribuit.utils.LogUtils;

import java.io.IOException;
import java.net.Socket;

/**
 * ServerGame Thread generated for each client
 */
public class ServerGameThread implements Runnable{


    private int gameID;
    private GameLogic gameLogic;

    /**
     * Create a new GameThread
     * @param socket Socket
     * @param gameID Number of Game
     * @throws IOException IOException Error
     */
    public ServerGameThread(Socket socket, int gameID) throws IOException {
        this.gameLogic = new GameLogic(socket);
        this.gameID = gameID;
    }

    /**
     * Inits the game
     */
    @Override
    public void run() {
        System.out.println("New client Connected! GameID: " + this.gameID);
        LogUtils logUtils = new LogUtils(Thread.currentThread().getName());
        gameLogic.setLogUtils(logUtils);

        // Log Thread Game info
        logUtils.info("New Game created with Thread: " + Thread.currentThread().getName());
        //Start the Game
        try {
            gameLogic.start();
        } catch (WrongActionException | IOException | UnknownActionException
                | MaxCashExceedException | NotEnoughCoinsException
                | ServerSideErrorException | NotEnoughCashException
                | NotClientCommandException | NoExistSuitException
                | NoExistRankException | PlayerCheatsException e) {

            logUtils.warn(e.getMessage());

            //Log the Error!
            logUtils.warn("Client disconnected from " + gameLogic.getSocket().getRemoteSocketAddress()
                    + " caused by an exception!");

            System.out.println("\n" + e.getMessage());
            System.out.println("Client disconnected from " + gameLogic.getSocket().getRemoteSocketAddress()
                    + " caused by an exception!");

        }finally {
            //When connection is closed then end every logHandler
            gameLogic.closeConnection();
            logUtils.closeHandler();
        }

    }

}
