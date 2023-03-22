package tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import tcp.server.ServerThread;


public class Client {
    private static String serverAddress = "localhost";
    private static int serverPort = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese 1 para el archivo de 100MB o 2 para el de 200MB:");
        String fileName = scanner.nextLine();
        scanner.close();

        if (fileName.equals("1")) {
            fileName = "100MB.txt";
        } else if (fileName.equals("2")) {
            fileName = "200MB.txt";
        } else {
            System.out.println("Opcion invalida");
            return;
        }

        try (Socket socket = new Socket(serverAddress, serverPort)) {


            // Send file name to server
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(fileName);



            // Receive file from server
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream("./arrival/" + fileName);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            fileOutputStream.flush();
            fileOutputStream.close();
            dataOutputStream.close();
            outputStream.close();
            socket.close();


            Socket socket2 = new Socket("localhost", 8000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

            // Receive Hash code from server
            String hashCode = in.readLine();
            in.close();
            socket2.close();


            // Calculate the file's hash code
            byte[] fileBytes = Files.readAllBytes(Paths.get("./arrival/" + fileName));
            String hashCode2 = ServerThread.getHashCode(fileBytes);

            System.out.println("Received hash code: " + hashCode);
            System.out.println("Calculated hash code: " + hashCode2);
            if (hashCode.equals(hashCode2)) {
                System.out.println("The file was received correctly");
            } else {
                System.out.println("The file was not received correctly");
            }



        } catch (IOException ex) {
            System.out.println("The client could not connect to the server");
            ex.printStackTrace();
        }
    }
}
