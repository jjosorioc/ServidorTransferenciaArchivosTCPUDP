package tcp.client;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import tcp.server.ServerThread;

public class Client {
    private static String serverAddress = "localhost";
    private static int serverPort = 5000;

    public static void main(String[] args) {

        try (
                // Open a socket to the server
                Socket socket = new Socket(serverAddress, serverPort);
                // Open input and output streams on the socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            // Read the filename from the user
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Ingrese 1 para el de 100MB, 2 para el de 200MB:");
            String opcion = userIn.readLine();

            out.println(opcion);

            // Receive the hash code from the server
            String hashCode = in.readLine();
            if (hashCode.equals("ServerThread: File not found.")) {
                System.out.println(hashCode);
                return;
            }

            String fileName = "";
            if (opcion.equals("1")) {
                fileName = "100MB.txt";
            } else if (opcion.equals("2")) {
                fileName = "200MB.txt";
            }

            // Receive the file from the server
            try (FileOutputStream fileOutputStream = new FileOutputStream(
                    System.getProperty("user.dir") + "/arrival/" + fileName)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.err.println("Client: Could not write file.");
            }

            // Verify the hash code of the received file
            byte[] fileBytes = new byte[(int) socket.getInputStream().available()];
            socket.getInputStream().read(fileBytes);
            String receivedHashCode = ServerThread.getHashCode(fileBytes);
            if (receivedHashCode.equals(hashCode)) {
                System.out.println("File received successfully. Hash code verified.");
            } else {
                System.err.println("File received but hash code verification failed");
                System.err.println("Expected hash code: " + hashCode);
                System.err.println("Received hash code: " + receivedHashCode);
            }

        } catch (IOException e) {
            System.err.println("Could not connect to server.");
        }
    }
}
