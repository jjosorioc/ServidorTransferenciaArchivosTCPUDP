package tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        DataInputStream in = null;
        DataOutputStream out = null;

        try {

            out = new DataOutputStream(serverSocket.getOutputStream());
            in = new DataInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("In or Out failed.");
        }

        // Read the filename from the client
        String fileName = null;
        try {
            fileName = in.readUTF();
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
        String filePath = System.getProperty("user.dir") + "/filesFolder/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                out.writeUTF("ServerThread: File not found.");
            } catch (IOException e) {
                System.err.println("Server: Could not send error message.");
            }
        } else {
            // Open a FileInputStream on the file
            try (FileInputStream fileInputStream = new FileInputStream(file)) {

                // Calculate the file's hash code
                byte[] fileBytes = new byte[(int) file.length()];

                System.out.println("File size: " + file.length() + " bytes.");
                fileInputStream.read(fileBytes);
                String hashCode = getHashCode(fileBytes);
                System.out.println("Hash code of file: " + hashCode);

                // Send the file and its hash code to the client
                out.writeUTF(hashCode);
                byte[] buffer = new byte[4096];

                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    System.out.println("ServerThread " + idThread + ": Sent " + bytesRead + " bytes.");
                }

                // Clean up resources
                fileInputStream.close();
                out.close();
                in.close();
                serverSocket.close();

            } catch (IOException e) {
                System.err.println("Could not read file.");
            }

        }

        System.out.println("ServerThread " + idThread + ": done");

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
