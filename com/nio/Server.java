package com.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private static ArrayList<SocketChannel> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        int port = 3000;
        Scanner sc = new Scanner(System.in);
        Selector selector = Selector.open();

        ServerSocketChannel serverCh = ServerSocketChannel.open();
        serverCh.configureBlocking(false);
        serverCh.bind(new InetSocketAddress(port));
        serverCh.register(selector, SelectionKey.OP_ACCEPT);

        System.out.printf("Server listen port %d ", port);
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel ch = (ServerSocketChannel) key.channel();
                    SocketChannel clientCh = ch.accept();
                    clientCh.configureBlocking(false);
                    System.out.println(clientCh.getRemoteAddress() + " join to server.");
                    clientCh.register(selector, SelectionKey.OP_READ);
                    clients.add(clientCh);
                }
                if (key.isReadable()) {
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(20);
                    ch.configureBlocking(false);
                    buffer.clear();
                    int n = ch.read(buffer);
                    if (n == -1) {
                        ch.close();
                        continue;
                    }
                    String message = new String(buffer.array());
                    int i = 1;
                    for (SocketChannel client : clients) {
                        ByteBuffer writeBuffer = ByteBuffer.allocate(20);
                        writeBuffer.put((message).getBytes());
                        System.out.println("Send msg to " + i + " " + client.getRemoteAddress() +
                                " : " + new String(writeBuffer.array()));
                        writeBuffer.flip();
                        client.write(writeBuffer);
                        i++;
                    }
                }

                it.remove();
            }
        }
    }
}
