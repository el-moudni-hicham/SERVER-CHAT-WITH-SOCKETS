package ma.enset.blocking.singleThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SimpleClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost",8080);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            Scanner sc = new Scanner(System.in);
            System.out.println("write a number :");
            int nb = sc.nextInt();
            os.write(nb);
            int result = is.read();
            System.out.println("result is : "+ result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
