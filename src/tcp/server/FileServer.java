package tcp.server;
import java.io.*;
import java.net.*;

public class FileServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server waiting for connection...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostName());

        FileInputStream fileInputStream = new FileInputStream("./filesFolder/100MB.txt");
        OutputStream outputStream = clientSocket.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        System.out.println("File sent.");
        outputStream.close();
        fileInputStream.close();
        clientSocket.close();
        serverSocket.close();
    }
}
