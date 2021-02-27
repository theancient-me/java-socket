package com.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) throws  Exception {

        // Server connect
         int port = 3000;
         String ip = "127.0.0.1";
         Scanner sc = new Scanner(System.in);


        Selector selector = Selector.open();
        SocketChannel clientCh = SocketChannel.open();
        clientCh.configureBlocking(false);
        clientCh.register(selector, SelectionKey.OP_CONNECT);
        ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
        clientCh.connect(new InetSocketAddress(ip,port));

        while (true) {
            System.out.print("msg : ");
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isConnectable()) {
                    SocketChannel ch = (SocketChannel) key.channel();
                    ch.finishConnect();
                    ByteBuffer buffer = ByteBuffer.allocate(20);
                    buffer.flip();
                    ch.write(buffer);

                    executorService.submit(() -> {

                        while (true) {
                            buffer.clear();
                            String msg = sc.nextLine();
                            buffer.put(msg.getBytes());
                            buffer.flip();
                            ch.write(buffer);
                        }
                    });

                    System.out.printf("Connecting with %s to %s \n",ch.getLocalAddress(),ch.getRemoteAddress());

                    ch.configureBlocking(false);
                    ch.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(20);
                    int n = ch.read(buffer);
                    if (n == -1) {
                        ch.close();
                        continue;
                    }
                    buffer.flip();
                    String msg = new String(buffer.array());
                    System.out.println(ch.getLocalAddress()+" says :"+msg.trim());
                }

                it.remove();
            }
        }

    }
}
