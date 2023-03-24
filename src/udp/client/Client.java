package udp.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Client {
    
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        // Datagram socket repreenta un UDP socket 
        DatagramSocket clientSocket = new DatagramSocket();
        // se obtiene la ip del 
        InetAddress IPAddress = InetAddress.getByName("localhost");
        //ususario selecciona que archivo enviar

        System.out.print("Enter file name: ");
        String fileName = inFromUser.readLine();
        if(fileName.equals("1")){
            fileName="100MB.txt";
        } 
        else if(fileName.equals("2")){
            fileName="small.txt";
        }
        else{
            System.out.println("Invalid option");
            return;
        }
       
        // se traduce el archivo a un array de bytes y se envia juntto con la longitud, la ip y el puerto
        byte[] sendData = fileName.getBytes();
        System.out.println(sendData);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        
        // se envia el paquete con el metodo send
        clientSocket.send(sendPacket);
        //El cliente espera para recibir respuesta del servidor. Guarda la info en otro socket
        byte[] receiveData = new byte[65507];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        clientSocket.setSoTimeout(5000); // set timeout to 5 seconds

        //El cliente guarda la info recibida en un archivo
        FileOutputStream fileOutputStream = new FileOutputStream(new File("received_" + fileName));
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
            if (packetLength <= 0) {
                break;
            }
            fileOutputStream.write(packetData, 0, packetLength);
            System.out.println("Received packet " + i);
            i+=1;
        }
        System.out.println("File received from " + receivePacket.getAddress() + " on port " + receivePacket.getPort());
        //cierra la conexion
        clientSocket.close ();
    }
    
}
