package tcp.server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            System.out.println("Server started.");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostName());

                // Receive file name from client
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String fileName = dataInputStream.readUTF();
                System.out.println("Client requested file: " + fileName);

                // Send file to client
                File file = new File("./filesFolder/"+fileName);
                if (file.exists()) {
                    OutputStream outputStream = socket.getOutputStream();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    System.out.println("File sent successfully.");
                } else {
                    System.out.println("File not found.");
                }
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
