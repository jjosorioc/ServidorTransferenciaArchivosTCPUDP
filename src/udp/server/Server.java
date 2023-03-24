package udp.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    public static void main(String args[]) throws Exception {
        // Datagram socket repreenta un UDP socket
        DatagramSocket serverSocket = new DatagramSocket(9876);

        while (true) {
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            Thread thread = new FileSender(serverSocket, receivePacket);
            thread.start();
        }
    }
    
}
