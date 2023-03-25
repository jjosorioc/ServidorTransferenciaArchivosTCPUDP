package tcp.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerMain {
    private static ServerSocket socket;
    private static int port = 5000;
    private static String ID = "ServerMain";
    private static ServerSocket communicationSocket;

    public static void main(String[] args) throws FileNotFoundException {

        int idThread = 0;
        try {
            socket = new ServerSocket(port);
            communicationSocket = new ServerSocket(8000);
            System.out.println("Ejecutando servidor TCP...");
        } catch (Exception e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        // Crear log file
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        String fileName = formattedDateTime + "-log.txt";
        PrintWriter log = new PrintWriter("./src/tcp/server/logs/" + fileName);
        log.close();
        while (true) {
            try {
                // Crea un delegado por cliente. Atiende por conexion.

                Socket sc = socket.accept();

                System.out.println(ID + " delegate " + idThread + ": accepting client - done");

                ServerThread d = new ServerThread(sc, idThread, communicationSocket, fileName);
                idThread++;
                d.start();
            } catch (IOException e) {
                System.out.println(ID + " delegate " + idThread + ": accepting client - ERROR");

            }
        }


    }
}
