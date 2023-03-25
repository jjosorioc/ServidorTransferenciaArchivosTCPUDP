package tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import tcp.server.ServerThread;


public class Client extends Thread {
    private static String serverAddress = "localhost";
    private static int serverPort = 5000;
    String fileName = null;
    public static int ID = 0;
    private int idClient;
    private int numConexiones;
    private String logFileName;
    private static Object lock = new Object();


    /**
     * Constructor para cuando se desea ejecutar un solo cliente
     */
    public Client(int numConexiones) {
        this.idClient = ID;
        this.numConexiones = 1;
        this.logFileName = "log.txt";
    }

    /**
     * Constructor para ejecutar el programa desde ClientMain
     * 
     * @param fileName
     */
    public Client(String fileName, int numConexiones, String logFileName) {
        this.fileName = fileName;
        this.numConexiones = numConexiones;
        this.logFileName = logFileName;

        synchronized (this) {
            this.idClient = ID;
            ID++;
        }

    }

    @Override
    public void run() {

        if (this.fileName == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese 1 para el archivo de 100MB o 2 para el de 200MB: ");
            fileName = scanner.nextLine();
            scanner.close();

            if (fileName.equals("1")) {
                fileName = "100MB.txt";
            } else if (fileName.equals("2")) {
                fileName = "200MB.txt";
            } else {
                System.out.println("Opcion invalida");
                return;
            }
        }

        try (Socket socket = new Socket(serverAddress, serverPort)) {


            // Send file name to server
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(fileName);



            // Receive file from server
            InputStream inputStream = socket.getInputStream();
            String originalFileName = fileName;
            fileName = "Cliente" + this.idClient + "-Prueba-" + numConexiones + ".txt";
            FileOutputStream fileOutputStream =
                    new FileOutputStream("./archivosRecibidos/" + fileName);
            byte[] buffer = new byte[4096];
            int bytesRead;

            long startTime = System.currentTimeMillis();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            long endTime = System.currentTimeMillis();

            // Close streams
            fileOutputStream.flush();
            fileOutputStream.close();
            dataOutputStream.close();
            outputStream.close();
            socket.close();


            Socket socket2 = new Socket(serverAddress, 8000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

            // Receive Hash code from server
            String hashCode = in.readLine();
            in.close();
            socket2.close();


            // Calculate the file's hash code
            byte[] fileBytes = Files.readAllBytes(Paths.get("./archivosRecibidos/" + fileName));
            String hashCode2 = ServerThread.getHashCode(fileBytes);

            System.out.println("Received hash code: " + hashCode);
            System.out.println("Calculated hash code: " + hashCode2);

            String exitoso = "EXITOSO";
            if (hashCode.equals(hashCode2)) {
                System.out.println("The file was received correctly");
            } else {
                exitoso = "FALLIDO";
                System.out.println("The file was not received correctly");
            }

            String logSentence = "Cliente " + this.idClient + " - Prueba " + this.numConexiones
                    + " para el archivo '" + originalFileName + "' ("
                    + ((double) Files.size(Paths.get("./archivosRecibidos/" + fileName)) / 1048576)
                    + " MB) - Tiempo de transferencia: " + (endTime - startTime) + " ms" + " - "
                    + exitoso;

            synchronized (lock) {
                PrintWriter logFile = new PrintWriter(
                        new FileWriter("./src/tcp/client/logs/" + logFileName, true));
                logFile.println(logSentence);
                logFile.close();
            }



        } catch (IOException ex) {
            System.out.println("The client could not connect to the server");
            ex.printStackTrace();
        }
    }
}
