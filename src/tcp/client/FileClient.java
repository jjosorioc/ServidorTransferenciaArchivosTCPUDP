package tcp.client;

import java.io.*;
import java.net.*;

public class FileClient {
    public static void main(String[] args) throws Exception {
        Socket clientSocket = new Socket("localhost", 5000);

        System.out.println("Connected to server: " + clientSocket.getInetAddress().getHostName());

        InputStream inputStream = clientSocket.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream("receivedFile.txt");

        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        System.out.println("File received.");
        fileOutputStream.close();
        inputStream.close();
        clientSocket.close();
    }
}
