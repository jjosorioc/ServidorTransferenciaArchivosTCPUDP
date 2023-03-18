package tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static ServerSocket socket;
    private static int port = 5000;
    private static String ID = "ServerMain";

    public static void main(String[] args) {

        int idThread = 0;
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        while (true) {
            try {
                // Crea un delegado por cliente. Atiende por conexion.
                // semaforo.acquire();
                Socket sc = socket.accept();
                System.out.println(ID + " delegate " + idThread + ": accepting client - done");

                ServerThread d = new ServerThread(sc, idThread);
                idThread++;
                d.start();
            } catch (IOException e) {
                System.out.println(ID + " delegate " + idThread + ": accepting client - ERROR");

            }
        }

    }
}