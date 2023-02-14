package ma.enset.blocking.MultiThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        new Client();
    }
    public Client() {
        try {
            Socket socket = new Socket("localhost",8080);

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os,true);

            new Thread(()->{
                try {
                    String request;
                    while (socket.isConnected() && (request = br.readLine()) != null)
                        System.out.println(request);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            Scanner sc = new Scanner(System.in);
            while (true){
                String request = sc.nextLine();
                pw.println(request);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
