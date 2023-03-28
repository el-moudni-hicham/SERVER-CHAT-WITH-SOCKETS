package ma.enset.nonblocking.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost",1111));
        Scanner scanner=new Scanner(System.in);
        new Thread(()->{
            while (true){
                ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                try {
                    socketChannel.read(byteBuffer);
                    String receivedMessage=new String(byteBuffer.array()).trim();
                    if(receivedMessage.length()>0){
                        System.out.println(receivedMessage);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Thread.sleep(500);

        while(true){
            System.out.println("Your Message : ");
            String message = scanner.nextLine();
            ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
            byteBuffer.put(message.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);

        }
    }
}