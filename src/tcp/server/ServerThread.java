package tcp.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private int idThread;
    private ServerSocket communicationServerSocket;
    private String logFileName;
    private static Object lock = new Object();

    public ServerThread(Socket sc, int idThread, ServerSocket cs, String fileName) {
        this.clientSocket = sc;
        this.idThread = idThread;
        this.communicationServerSocket = cs;
        this.logFileName = fileName;
    }

    @Override
    public void run() {
        System.out.println("ServerThread " + idThread + ": running");


        try (InputStream inputStream = clientSocket.getInputStream()) {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String fileName = dataInputStream.readUTF();

            // Send the file to the client
            String filePath = System.getProperty("user.dir") + "/filesFolder/" + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                OutputStream outputStream = clientSocket.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(file);



                byte[] buffer = new byte[4096];

                int bytesRead;
                long startTime = System.currentTimeMillis();
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                long endTime = System.currentTimeMillis();


                String logSentence =
                        "Thread " + this.idThread + " sent the file called '" + fileName + "' ("
                                + ((double) Files.size(Paths.get("./filesFolder/" + fileName))
                                        / 1048576)
                                + " MB) to " + clientSocket.getInetAddress() + " in "
                                + (endTime - startTime) + " miliseconds.\n";


                synchronized (lock) {
                    PrintWriter logFile = new PrintWriter(
                            new FileWriter("./src/tcp/server/logs/" + logFileName, true));
                    logFile.println(logSentence);
                    logFile.close();
                }



                // Close streams
                dataInputStream.close();
                inputStream.close();
                System.out.println("Server: File sent successfully.");
                outputStream.close();
                fileInputStream.close();
                clientSocket.close();


                Socket communicationSocket = null;
                PrintWriter out = null;
                try {
                    communicationSocket = this.communicationServerSocket.accept();
                    out = new PrintWriter(communicationSocket.getOutputStream(), true);

                } catch (IOException e) {
                    System.err.println("Could not listen on port: 8000.");
                }

                // Calculate the file's hash code
                byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

                String hashCode = getHashCode(fileBytes);
                System.out.println("Hash code of file: " + hashCode);
                // Send the file and its hash code to the client
                out.println(hashCode);

                out.close();
                communicationSocket.close();



            }
        } catch (IOException e) {
            System.err.println("ServerThread " + idThread + ": Accept failed.");
            e.printStackTrace();
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
