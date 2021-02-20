package com.blocking;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws  Exception{

        Socket clientSocket = new Socket();
        Scanner scannerInput = new Scanner(System.in);

        try {
            clientSocket.connect(new InetSocketAddress("127.0.0.1", 8080));
            System.out.printf("connected from port %d\n", clientSocket.getLocalPort());
        } catch (Exception e){
            System.out.println(e);
        }
        Server server = new Server(clientSocket);

        new Thread(server).start();

        System.out.println("Waiting for user input");

        while(true){
            String input = scannerInput.nextLine();

            if(input.equalsIgnoreCase("close")){
                clientSocket.getOutputStream().write((input + "\n").getBytes());
                clientSocket.getOutputStream().flush();
                break;
            }

            clientSocket.getOutputStream().write((input+"\n").getBytes());
            clientSocket.getOutputStream().flush();
        }
        clientSocket.close();
    }
}