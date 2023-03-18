package tcp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerThread extends Thread {
    private Socket serverSocket;
    private int idThread;

    public ServerThread(Socket sc, int idThread) {

        this.serverSocket = sc;
        this.idThread = idThread;
    }

    @Override
    public void run() {
        System.out.println("ServerThread " + idThread + ": running");

        // Open input and output streams on the client socket
        PrintWriter out = null;
        BufferedReader in = null;

        try {

            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("In or Out failed.");
        }

        // Read the filename from the client
        String fileName = null;
        try {
            fileName = in.readLine();
        } catch (IOException e) {
            System.err.println("Read failed.");
        }
        if (fileName.equals("1")) {
            fileName = "100MB.txt";
        } else if (fileName.equals("2")) {
            fileName = "200MB.txt";
        }
        System.out.println("Sending file: " + fileName);

        // Send the file to the client
        File file = new File(System.getProperty("user.dir") + "/filesFolder/" + fileName);
        if (!file.exists()) {
            out.println("ServerThread: File not found.");
        } else {
            try (// Open a FileInputStream on the file
                    FileInputStream fileInputStream = new FileInputStream(file)) {
                // Calculate the file's hash code
                byte[] fileBytes = new byte[(int) file.length()];
                fileInputStream.read(fileBytes);
                String hashCode = getHashCode(fileBytes);
                System.out.println("Hash code of file: " + hashCode);

                // Send the file and its hash code to the client
                out.println(hashCode);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    serverSocket.getOutputStream().write(buffer, 0, bytesRead);
                }

                // Clean up resources
                fileInputStream.close();
            } catch (IOException e) {
                System.err.println("Could not read file.");
            }

            try {
                out.close();
                in.close();
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close.");
            }

        }

    }

    public static String getHashCode(byte[] fileBytes) {
        String hashCode = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(fileBytes);
            hashCode = bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashCode;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
