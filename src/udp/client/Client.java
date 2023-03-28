package udp.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Client extends Thread{
    private int id;
    private String fileName;
    private int total;
    public Client(int id,String fileName,int total){
        this.id=id; 
        this.fileName=fileName;
        this.total=total;
    }
    
    public void run(){
        
        // Datagram socket repreenta un UDP socket 
        DatagramSocket clientSocket=null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Error creating socket");
        }
        
        // se obtiene la ip del 
        InetAddress IPAddress=null;
        try {
            IPAddress = InetAddress.getByName("192.168.1.143");
        } catch (UnknownHostException e) {
            System.out.println("Error getting IP address");
        }
        
        // se traduce el archivo a un array de bytes y se envia juntto con la longitud, la ip y el puerto
        byte[] sendData = fileName.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        
        // se envia el paquete con el metodo send
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("Error sending packet");
        }

        //El cliente espera para recibir respuesta del servidor. Guarda la info en otro socket
        byte[] receiveData = new byte[65507];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            clientSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("Error receiving packet");
        }

        //Se pone un time out de 5 segundos
        try {
            clientSocket.setSoTimeout(5000);
        } catch (SocketException e) {
            System.out.println("Error setting timeout");
        } 

        Instant start=Instant.now();
        int size=0;
        try (//El cliente guarda la info recibida en un archivo
        FileOutputStream fileOutputStream = new FileOutputStream(new File("archivosRecibidos/" + id + "-Prueba" + total+".txt"))) {
            int i=1;
            while(true){
                try{
                    clientSocket.receive(receivePacket);
                }catch(SocketTimeoutException e){
                    System.out.println("Timeout ocurred. no more data to receive.");
                    break;
                }
                byte[] packetData = receivePacket.getData();
                int packetLength = receivePacket.getLength();
                size+=packetLength;
                if (packetLength <= 0) {
                    break;
                }
                fileOutputStream.write(packetData, 0, packetLength);
                System.out.println("Received packet " + i);
                i+=1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Instant end=Instant.now();
        Duration timeElapsed=Duration.between(start, end);
        
        //fecha actual
        Date fechaActual= new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String fechaFormateada = formato.format(fechaActual);
        try {
            FileWriter file=new FileWriter(new File("./src/udp/client/logs/"+fechaFormateada+"-log.txt"));
            file.write("Tiempo de descarga: "+timeElapsed.toMillis()+" ms");
            file.write("\nNombre archivo : "+fileName);
            file.write("\nTamaño archivo : "+size);
            file.write("\nExitoso");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File received from " + receivePacket.getAddress() + " on port " + receivePacket.getPort());
        //cierra la conexion
        clientSocket.close ();
    }
    
}
