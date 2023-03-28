package ma.enset.nonblocking.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class SingleThreadNonBlockingChatServer {
    private Map<SocketChannel,Integer> socketChannels=new HashMap<>();
    private int clientsCount;
    public static void main(String[] args) throws Exception {
        new SingleThreadNonBlockingChatServer();
    }
    public SingleThreadNonBlockingChatServer(){
        this.startServer();
    }
    public void startServer(){
        try {
            Selector selector=Selector.open();
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("0.0.0.0",3333));
            System.out.println("Server start on port 3333");
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                int readyChannels = selector.select();
                if (readyChannels==0) continue;
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()){
                        handleForAccept(selector,selectionKey);
                    } else if(selectionKey.isReadable()){
                        handleForRead(selector,selectionKey);
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleForAccept(Selector selector, SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        ++clientsCount;
        socketChannels.put(socketChannel,clientsCount);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);
        System.out.println("New Connection from : "+clientsCount);
        sendMessage(String.format("Welcome you are client number %s",clientsCount),socketChannel);
    }
    private void handleForRead(Selector selector, SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if (socketChannel.isConnected()){
            int read = socketChannel.read(byteBuffer);
        if (read == -1) {
            System.out.println("Client disconnected ....");
            socketChannels.remove(socketChannel);
            socketChannel.close();
            socketChannel.keyFor(selector).channel();
        } else {
            String request = new String(byteBuffer.array()).trim();
            if (request.length() > 0) {
                String message = request;
                List<Integer> destinationList = new ArrayList<>();
                String[] requestItems = request.split(">");
                if (requestItems.length == 2) {
                    String destination = requestItems[0];
                    message = requestItems[1];
                    if (destination.trim().contains(",")) {
                        String[] destinations = destination.trim().split(",");
                        for (String d : destinations) {
                            destinationList.add(Integer.parseInt(d));
                        }
                    } else {
                        destinationList.add(Integer.parseInt(destination));
                    }
                }
                broadCastMessage(message, socketChannel, destinationList);
            }
        }
    }
    }
    private void broadCastMessage(String message, SocketChannel from, List<Integer> destinations) throws IOException {
        for (SocketChannel socketChannel:socketChannels.keySet()){
            int clientId=socketChannels.get(socketChannel);
            boolean all=destinations.size()==0;
            if(!socketChannel.equals(from) && (destinations.contains(clientId) || all)){
                ByteBuffer byteBufferResponse=ByteBuffer.allocate(1024);
                int fromId=socketChannels.get(from);
                String formattedMessage=String.format("user %s : %s",fromId,message);
                byteBufferResponse.put(formattedMessage.getBytes());
                byteBufferResponse.flip();
                socketChannel.write(byteBufferResponse);
            }
        }
    }
    private void sendMessage(String message, SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBufferResponse=ByteBuffer.allocate(1024);
        byteBufferResponse.put(message.getBytes());
        byteBufferResponse.flip();
        socketChannel.write(byteBufferResponse);
    }
}
