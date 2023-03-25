package tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain {

    public static void main(String[] args) {

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
            fileName = "small.txt";
        } else {
            System.out.println("Invalid option");
            return;
        }
        System.out.println("Enter number of clients: ");
        int total = 0;
        try {
            total = Integer.parseInt(inFromUser.readLine());
        } catch (IOException e) {
            System.out.println("Error reading input");
        }

        for (int i = 0; i < total; i++) {
            Client client = new Client(fileName);
            client.start();
        }
    }
}
