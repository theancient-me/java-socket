package com.blocking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Server implements Runnable {
    private Socket serverSocket;
    private BufferedReader in;
    private PrintWriter out;
    public Server(Socket s) throws Exception {
        serverSocket = s;
        try{
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            out = new PrintWriter(serverSocket.getOutputStream(), true);
        }catch (Exception e){
        }
    }

    @Override
    public void run(){
        this.serverSocket = serverSocket;
        try{
            while (true) {
                String serverResponse = in.readLine();
                System.out.print(serverResponse + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}