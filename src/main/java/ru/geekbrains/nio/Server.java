package ru.geekbrains.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {


    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost",9000));
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start");


        while (true) {
            selector.select();
            System.out.println("New event");
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isAcceptable()) {
                    System.out.println("New selector acceptable event");
                    register(selector, serverSocketChannel);
                } if (selectionKey.isReadable()) {
                    System.out.println("New selector readable event");
                    readAndEchoMessage(selectionKey);
                }
            }iterator.remove();
        }
    }
    public void readAndEchoMessage(SelectionKey selectionKey) throws IOException {
        SocketChannel client = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        client.read(byteBuffer);
        client.write(byteBuffer);
        String message = new String(byteBuffer.array());
        System.out.println("Your message: " + message.trim());
        byteBuffer.clear();
    }


    public void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client =serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector,SelectionKey.OP_READ);
        System.out.println("New client is connected");
    }


}