package ma.enset.blocking.MultiThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadBlockingServer extends Thread{
    int clientCounter;
    public static void main(String[] args) {
        new MultiThreadBlockingServer().start();
    }

    @Override
    public void run() {
        System.out.println("Server start on port 2222");
        try {
            ServerSocket ss = new ServerSocket(2222);
            while (true){
                Socket socket = ss.accept();
                ++ clientCounter;
                new Conversastion(socket,clientCounter).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Conversastion extends Thread{
        private Socket socket;
        private int clientId;
        public Conversastion(Socket socket,int clientId){
            this.socket = socket;
            this.clientId = clientId;
        }
        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os,true);

                String ip = socket.getRemoteSocketAddress().toString();
                System.out.println("new client Connection with ID: "+ clientId+ " IP adress : "+ ip);
                pw.println(" welcome, you are client "+ clientId);
                String request;
                while (!(request = br.readLine()).equals(null)){
                    //System.out.println("new request IP adress : "+ ip+ "Request : "+ request);
                    String response = "size "+ request.length();
                    pw.println(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
