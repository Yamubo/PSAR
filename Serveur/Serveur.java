package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import Fichier.FichierServeur;

public class Serveur {
    private static final int PORT = 12345;
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
                            username =  addUser(tokens[1],out);
                            System.out.println(username);
                            break;
                        case "ADD" :
                            add(tokens);
                            break;
                        case "SYNC" :
                            sync(out);
                            break;
                        case "SUPP" :
                            suppr(tokens,username,out);
                            break;
                        case "MOD" :
                            mod(tokens,username);
                        case "MODASK" :
                            modask(tokens,username,out);
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

        private static int add(String [] tokens){
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

        private static void sync(PrintWriter out){
            String[] contenu = fs.getContent();
            Integer [] ids = fs.getAllId();

            if(contenu != null)
                for(int i = contenu.length-1 ; i>-1 ; i--){
                    out.println("SYNC:" + ids[i] + ":" + contenu[i]);
                }

            out.println("ENDSYNC:0");
        }

        private static void suppr(String [] tokens , String username,PrintWriter out){
            try{
                if(tokens.length < 2){
                    System.out.println("Error req SUPP : " + username);
                    out.println("REFUS:0");
                    return;
                }
                int id = Integer.parseInt(tokens[1]);
                if(fs.isUnlock_and_exist(id, username)){
                    fs.addLock(id, username);
                    fs.supprimerLigne(id, username);
                    out.println("ACCEPT:0");
                    sendSuppr(id);
                }else{
                    out.println("REFUS:0");
                    System.out.println("Error req SUPP ligne lock: " + username);
                }

            }catch(NumberFormatException e){
                System.out.println("Error req SUPP : " + username);
                out.println("ERROR:0");
            }
        }

        private static void mod(String [] tokens,String username){
            try{
                if(tokens.length < 3){
                    System.out.println("Error req MOD : " + username);
                    return;
                }
                int id = Integer.parseInt(tokens[1]);
                if(fs.isUnlock_and_exist(id, username)){
                    fs.modifierLigne(id, tokens[2], username);
                    sendUpdate(tokens[2], id);
                    fs.unlock(id, username);
                }else{
                    System.out.println("Error req MOD ligne lock: " + username);
                }

            }catch(NumberFormatException e){
                System.out.println("Error req Mod : " + username);
            }
        }

        private static void modask(String [] tokens , String username , PrintWriter out){
            try{
                if(tokens.length < 2){
                    System.out.println("Error req MODASK : " + username);
                    out.println("REFUS:0");
                    return;
                }
                int id = Integer.parseInt(tokens[1]);
                if(fs.isUnlock_and_exist(id, username)){
                    fs.addLock(id, username);
                    String tmp = fs.getSpecContent(id);
                    if(tmp == null)
                        tmp= " ";
                    out.println("ACCEPT:"+tmp);
                }else{
                    out.println("REFUS:0");
                    System.out.println("Error req MODASK ligne lock: " + username);
                }

            }catch(NumberFormatException e){
                System.out.println("Error req MODASK : " + username);
                out.println("ERROR:0");
            }
        }

        private static void sendUpdate(String contenu , int id){
            users.forEach((u,out) -> {out.println("UPDATE:" + id + ":" +contenu) ; System.out.println("Update send") ;});
        }

        private static void sendCreate(int idprec , String contenu , int id ){
            users.forEach((u,out) -> {out.println("CREAT:" + idprec + ":" +contenu + ":" + id) ; System.out.println("creat send") ;});
        }

        private static void sendSuppr(int id){
            users.forEach((u,out) -> out.println("SUPP:" + id ));
        }

        private static String addUser(String username,PrintWriter out){
            String name = username;
            if(users.containsKey(name)){
                name = name + connectedUsers;
            }
            users.put(name, out);
            return name;
        }

    }
}