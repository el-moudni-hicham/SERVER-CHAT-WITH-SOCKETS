package ma.enset.blocking.singleThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("I'm waiting new connection !");
            Socket socket = ss.accept();
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            System.out.println("I'm waiting your data");
            int nb = is.read();
            System.out.println("I'm sending result");
            int result = nb + 1;
            os.write(result);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
