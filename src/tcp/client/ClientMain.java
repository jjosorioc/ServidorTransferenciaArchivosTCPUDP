package tcp.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientMain {

    public static void main(String[] args) throws FileNotFoundException {

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Ingrese 1 para el archivo de 100MB o 2 para el de 200MB: ");
        String fileName = "";
        try {
            fileName = inFromUser.readLine();
        } catch (IOException e) {
            System.out.println("Error reading input");
        }
        if (fileName.equals("1")) {
            fileName = "100MB.txt";
        } else if (fileName.equals("2")) {
            fileName = "200MB.txt";
        } else {
            System.out.println("Invalid option");
            return;
        }
        System.out.println("Enter number of clients: ");
        int numConexiones = 0;
        try {
            numConexiones = Integer.parseInt(inFromUser.readLine());
        } catch (IOException e) {
            System.out.println("Error reading input");
        }

        // Crear log file
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        String logFileName = formattedDateTime + "-log.txt";
        PrintWriter log = new PrintWriter("./src/tcp/client/logs/" + logFileName);
        log.close();

        for (int i = 0; i < numConexiones; i++) {
            Client client = new Client(fileName, numConexiones, logFileName);
            client.start();
        }
    }
}
