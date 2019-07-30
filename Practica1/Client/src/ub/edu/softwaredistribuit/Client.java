package ub.edu.softwaredistribuit;

import ub.edu.softwaredistribuit.commons.GameMode;

import java.io.IOException;
import java.net.Socket;

/**
 * Client Main class
 */
public class Client {

    public Client() {
    }

    public static void main(String[] args) {

        String serverIP = "";
        int port = 0;
        GameMode gameMode = GameMode.MANUAL; // Default gamemode is at 0 (MANUAL)

        // Check arguments
        try{
            if (args.length == 4 && args[0].equals("-s") && args[2].equals("-p")) {

                serverIP = args[1];
                port = Integer.parseInt(args[3]);

            } else if (args.length == 6 && args[0].equals("-s") && args[2].equals("-p") && args[4].equals("-i")) {

                serverIP = args[1];
                port = Integer.parseInt(args[3]);
                int mode = Integer.parseInt(args[5]);
                gameMode = GameMode.fromValue(mode);

                if (mode > 2 || mode < 0) {
                    System.out.println("Us: java Client -s <maquina_servidora> -p <port> [-i 0|1|2]");
                    System.exit(1);
                }

            } else {
                System.out.println("Us: java Client -s <maquina_servidora> -p <port> [-i 0|1|2]");
                System.exit(1);
            }


            Socket client = new Socket(serverIP, port);
            client.setSoTimeout(30 * 1000);
            System.out.println("Client connected to " + serverIP + " on port " + port);

            // Create a new GameClientThread logic game
            // Not really necessary but it would be a good idea if in the future we want
            // to let our client to be able to play multiple games at the same time
            new Thread(new GameClientThread(client, gameMode)).start();

        } catch (NumberFormatException | IOException e){
            e.printStackTrace();
        }


    }

}
