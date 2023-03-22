package tcp.client;

import java.io.*;

import java.net.Socket;
import java.util.Scanner;

public class FileClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = scanner.nextLine();

        try (Socket socket = new Socket("localhost", 5000)) {
            // Send file name to server
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(fileName);

            // Receive file from server
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream("./arrival/" + fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            dataOutputStream.close();
            outputStream.close();
            socket.close();

            Socket socket2 = new Socket("localhost", 8000);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            String confirmation = in.readLine();
            System.out.println(confirmation);
            in.close();

        } catch (IOException ex) {
            System.out.println("Error Client: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
