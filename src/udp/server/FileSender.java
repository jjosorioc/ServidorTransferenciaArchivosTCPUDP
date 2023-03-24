package udp.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class FileSender extends Thread{
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private int chunksize=65507; //64kb - 8bytes header

    public FileSender(DatagramSocket serverSocket, DatagramPacket receivePacket) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
    }

    public void run() {
        try {
            String fileName = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String filePath = System.getProperty("user.dir") + "/filesFolder/" + fileName;
            System.out.println("Received file name: " + filePath);

            //obtiene ip y puerto del cliente que envio la solicitud
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
            //Representa en un objeto el archivo obtenido
            File file = new File(filePath);

            //Se lee el archivo y se guarda en un array de bytes
            FileInputStream fileInputStream = new FileInputStream(file);

            int fileSize = (int) file.length();
            int numChunks=(int) Math.ceil((double) fileSize/chunksize);
            
            byte[] numChunksData=ByteBuffer.allocate(4).putInt(numChunks).array();
            DatagramPacket numChunksPacket=new DatagramPacket(numChunksData, numChunksData.length,IPAddress,port);
            serverSocket.send(numChunksPacket);

            //envia el archivo al cliente
            byte[] fileBytes = new byte[chunksize];
            int bytesRead=0;
            for(int i=0;i<numChunks;i++){
                bytesRead=fileInputStream.read(fileBytes,0,chunksize);
                System.out.println("Sending packet " + i+1 + " of " + numChunks + " with " + bytesRead + " bytes");
                DatagramPacket sendPacket =new DatagramPacket(fileBytes, bytesRead, IPAddress, port);
                serverSocket.send(sendPacket);
            }
            DatagramPacket sendPacket=new DatagramPacket(new byte[0], 0, IPAddress, port);
            serverSocket.send(sendPacket);
            fileInputStream.close();
            
           

            System.out.println("File sent to " + IPAddress + " on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
