package ma.enset.blocking.MultiThread.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiThreadChatServer extends Thread{
    private List<Conversastion> conversastionList = new ArrayList<>();
    int clientCounter;
    public static void main(String[] args) {
        new MultiThreadChatServer().start();
    }

    @Override
    public void run() {
        System.out.println("Server start on port 2222");
        try {
            ServerSocket ss = new ServerSocket(2222);
            while (true){
                Socket socket = ss.accept();
                ++ clientCounter;
                Conversastion conversastion = new Conversastion(socket,clientCounter);
                conversastionList.add(conversastion);
                conversastion.start();
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
                pw.println(" welcome, you are user number "+ clientId);
                String request;
                while ((request = br.readLine()) != null){
                    //System.out.println("new request IP adress : "+ ip+ "Request : "+ request);
                    List<Integer> clientsTo = new ArrayList<>();
                    String message;
                    if(request.contains(">")) {
                        String[] items = request.split(">");
                        String clients = items[0];
                        message = "From "+clientId+" : "+items[1];
                        if(clients.contains(",")){
                            String[] clientIds = clients.split(",");
                            for (String id:clientIds) {
                                clientsTo.add(Integer.parseInt(id));
                            }
                        }else{
                            clientsTo.add(Integer.parseInt(clients));
                        }
                    }else {
                        clientsTo = conversastionList.stream().map(c ->c.clientId).collect(Collectors.toList());
                        message = "From "+clientId+" : "+request;
                    }
                    broadcastMessage(message, this,clientsTo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void broadcastMessage(String message, Conversastion from, List<Integer> clients){
        try {
            for(Conversastion conversastion:conversastionList) {
                if(conversastion != from && clients.contains(conversastion.clientId)) {
                    Socket socket = conversastion.socket;
                    OutputStream os = socket.getOutputStream();
                    PrintWriter pw = new PrintWriter(os, true);
                    pw.println(message);
                }
            }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}
