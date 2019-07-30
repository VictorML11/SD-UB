package ub.edu.softwaredistribuit;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Server {

    public Server() {
    }

    public static void main(String[] args) {
        Socket s = null;
        if (args.length == 2 && args[0].equals("-p")) {

            try {

                int port = Integer.parseInt(args[1]);
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server Listening at port: "+ port);

                int gameCounter = 0;

                do {
                    System.out.println("WAITING FOR CLIENTS...");
                    s = serverSocket.accept();
                    gameCounter++;
                    new Thread(new ServerGameThread(s, gameCounter)).start();

                } while (true);

            } catch (IOException | NumberFormatException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }finally {
                try {
                    assert s != null;
                    s.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Us: java Server -p <port>");
            System.exit(1);
        }
    }
}
