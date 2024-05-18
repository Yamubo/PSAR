package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Fichier.FichierServeur;


public class tpServeur {
    private static final int PORT = 12345;
    private static Map<String, List<String>> files = new HashMap<>();
    private static Map<String, PrintWriter> users = new HashMap<>();
    private static int connectedUsers = 0;
    private static FichierServeur fs = new FichierServeur();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectedUsers++;
                System.out.println("Nouvelle connexion : " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    String[] tokens = inputLine.split(":");
                    String action = tokens[0];
                    System.out.println(action);
                    switch (action.toUpperCase()) {
                        case "USER":
                            username = tokens[1] + connectedUsers;
                            System.out.println(username);
                            users.put(username,out);
                            break;
                        case "ADD" :
                            add(tokens);
                            break;
                        case "SYNC" :
                            sync(out);
                            break;
                        default:
                            System.out.println("Action invalide");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    users.remove(username);
                    connectedUsers--;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        
        public static int add(String [] tokens){
            try{
                int idprec = Integer.parseInt(tokens[1]);
                int newid = fs.ajouterLigne(idprec, tokens[2]);
                sendCreate(idprec , tokens[2] , newid );
                return newid;
            }catch(NumberFormatException e ){
                System.out.println("add error : not int ");
            }
            return -1;
        }

        public static void sync(PrintWriter out){
            String[] contenu = fs.getContent();
            Integer [] ids = fs.getAllId();

            if(contenu != null)
                for(int i = contenu.length-1 ; i>-1 ; i--){
                    out.println("SYNC:" + ids[i] + ":" + contenu[i]);
                }

            out.println("ENDSYNC:0");
        }

        public static void sendUpdate(String contenu , int id){
            users.forEach((u,out) -> {out.println("UPDATE:" + id + ":" +contenu) ; System.out.println("Update send") ;});
        }

        public static void sendCreate(int idprec , String contenu , int id ){
            users.forEach((u,out) -> {out.println("CREAT:" + idprec + ":" +contenu + ":" + id) ; System.out.println("creat send") ;});
        }

        public static void sendSuppr(int id){
            users.forEach((u,out) -> out.println("SUPP:" + id ));
        }

    
    }
}