package tcp.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            ServerSocket communication = new ServerSocket(8000);
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
                File file = new File("./filesFolder/" + fileName);
                if (file.exists()) {
                    OutputStream outputStream = socket.getOutputStream();
                    FileInputStream fileInputStream = new FileInputStream(file);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    // Close streams
                    dataInputStream.close();
                    inputStream.close();
                    System.out.println("Server: File sent successfully.");
                    outputStream.close();
                    fileInputStream.close();
                    socket.close();

                    
                    Socket socket2 = communication.accept();

                    PrintWriter out = new PrintWriter(socket2.getOutputStream(), true);

                    out.println("COM: File sent successfully.");
                    out.flush();
                    out.close();

                    
                } else {
                    System.out.println("File not found.");
                }

            }
        } catch (IOException ex) {
            System.out.println("Error Server: " + ex.getMessage());
        }
    }
}
