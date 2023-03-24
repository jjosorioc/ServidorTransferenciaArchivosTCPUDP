package udp.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileSender extends Thread{
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;

    public FileSender(DatagramSocket serverSocket, DatagramPacket receivePacket) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
    }

    public void run() {
        try {
            String fileName = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String filePath = System.getProperty("user.dir") + "/filesFolder/" + fileName;
            System.out.println("Received file name: " + filePath);
            
            //Representa en un objeto el archivo obtenido
            File file = new File(filePath);
            //Se lee el archivo y se guarda en un array de bytes
            FileInputStream fileInputStream = new FileInputStream(file);
            int fileSize = (int) file.length();
            byte[] fileBytes = new byte[fileSize];
            fileInputStream.read(fileBytes);

            //obtiene ip y puerto del cliente que envio la solicitud
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
            //envia el archivo al cliente
            byte[] sendData = fileBytes;
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);

            System.out.println("File sent to " + IPAddress + " on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
