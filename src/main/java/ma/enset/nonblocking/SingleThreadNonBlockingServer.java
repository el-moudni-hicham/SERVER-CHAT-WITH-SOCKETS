package ma.enset.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SingleThreadNonBlockingServer {
    public static void main(String[] args) throws IOException {
        // Demarage
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("0.0.0.0", 3333));
        System.out.println("Server start on port 3333");
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            int channelNb = selector.select();
            if (channelNb == 0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    handleAccept(selectionKey, selector);
                } else if (selectionKey.isReadable()) {
                    handleReadWrite(selectionKey, selector);
                }
                iterator.remove();
            }
        }
    }
    private static void handleAccept(SelectionKey selectionKey, Selector selector) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println(String.format("New Connection %s number from %s ",socketChannel.getRemoteAddress().toString()));
    }
    private static void handleReadWrite(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int nbOctets = socketChannel.read(byteBuffer);
        if(nbOctets == -1){
            System.out.println(String.format("The client %s has been disconnected ", socketChannel.getRemoteAddress().toString()));
        }
        String request = new String(byteBuffer.array()).trim();
        //System.out.println(String.format("New Request %s from %s ", request, socketChannel.getRemoteAddress().toString()));
        String response = new StringBuffer(request).reverse().toString().toUpperCase()+"\n";
        ByteBuffer byteBufferResponse = ByteBuffer.allocate(1024);
        byteBufferResponse.put(response.getBytes());
        // flip for pass from read to write mode
        byteBufferResponse.flip();
        socketChannel.write(byteBufferResponse);
    }
}
