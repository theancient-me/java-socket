package com.blocking;


import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));
        System.out.println("Listening to port: 8080");

        while (true){
            System.out.println("Waiting for client connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.printf("Client connected %s:%d\n", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());

            ClientHandler clientThread = new ClientHandler(clientSocket, clients);
            clients.add(clientThread);
            pool.execute(clientThread);
        }

    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ArrayList<ClientHandler> clients;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;
    }

    @Override
    public void run(){
        try{
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            while (scanner.hasNextLine()){
                String clientInput = scanner.nextLine();
                System.out.printf("Got from %d: %s\n", clientSocket.getPort(),clientInput);
                String msg = clientSocket.getPort()+ " says: " + clientInput;
                if(clientInput.equalsIgnoreCase("close")){
                    System.out.println("Port: "+ clientSocket.getPort() + " disconnect...");
                    clients.remove(clientSocket);
                    break;
                }else{
                    for (ClientHandler aClient: clients){
                        if(aClient.clientSocket.getPort() != clientSocket.getPort()){
                            aClient.clientSocket.getOutputStream().write((msg + "\n").getBytes());
                            aClient.clientSocket.getOutputStream().flush();
                        }

                    }
                }
                clientSocket.getOutputStream().flush();
            }
            scanner.close();
            clientSocket.close();
        }catch (Exception e){
            //do noting
        }
    }

}
