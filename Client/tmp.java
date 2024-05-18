package Client;

import Fichier.FichierClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.io.Console;

public class tmp {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static String username;
    private static List<String> syncwait = new ArrayList<String>();
    private static List<String> commandes = new ArrayList<String>();
    private static FichierClient lp  = new FichierClient();


    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())) ){
            
            Reception tm = new Reception(in);
            new Thread(tm).start();
            System.out.println("Client connecté au serveur !");

        
           
            Console console;
            if ((console = System.console()) == null) {
                
                System.exit( 1 );
            }
            username = console.readLine("entrer un username : \n");
            out.println("USER:"+username);
            out.println("SYNC:0");
            listeReq();

            boolean fin = true;
            while (fin) {
                String ligne = console.readLine("Quel est votre requête ? \n");
                String [] req = ligne.toLowerCase().split(" "); 
                if(req.length >= 1 ){
                    switch (req[0]) {
                        case "mod":
                            if(req.length > 1)
                                mod(lp, req[1], console);
                            else 
                                System.out.println("mauvais nombre d'arguments \n");
                            break;
                        case "suppr":
                            if(req.length > 1)
                                suppr(lp, req[1], console);
                            else 
                                System.out.println("mauvais nombre d'arguments \n");
                            break;

                        case "add":
                            if(req.length > 1)
                                add(lp, req[1], console,out);
                            else 
                                System.out.println("mauvais nombre d'arguments \n");
                            break; 
                        case "printall":
                            System.out.println("print all \n");
                            lp.printAll();
                            break; 
                        case "print":
                            if(req.length < 2) {
                                System.out.println("mauvais nombre d'arguments \n");
                                break;
                            }
                        
                            int id = 0;
                            try{
                                id = Integer.parseInt(req[1]);
                            }catch(NumberFormatException e ){
                                System.out.println("Argument n'est pas un nombre \n");
                                break;
                            }
                            lp.print(id);
                            break; 
                        case "exit":
                            System.out.println("Exit \n");
                            fin=false;
                            break;  
                        case "help":
                            System.out.println("Help \n");
                            listeReq();
                            break; 
                    
                        default:
                            System.out.println("Error : mauvaise commande \n");
                            break;
                    }
                }
           
            }

            tm.stop = true;
            System.exit( 1 );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Reception implements Runnable {
        BufferedReader in ;
        boolean stop = false;

        public Reception(BufferedReader rep){
            this.in = rep;
        }

        @Override
        public void run(){
            try{
                String tmp ;
                boolean sync = false;
                while( stop == false){
                    tmp = in.readLine();
                    if(tmp == null )continue;
                    if(stop) break;
                    String [] rep = tmp.split(":");
                    switch (rep[0]) {
                        case "UPDATE":
                            if(!sync)
                                lp.update(Integer.parseInt(rep[1]), rep[2]);
                            else syncwait.add(tmp);
                            break; 
                        case "CREAT" :
                            if(!sync)
                                lp.ajouterLigne(Integer.parseInt(rep[1]), rep[2], Integer.parseInt(rep[3]));
                            else syncwait.add(tmp);
                            break;
                        case "SUPP" :
                            if(!sync)
                                lp.supprimerLigne(Integer.parseInt(rep[1]));
                            else syncwait.add(tmp);
                            break;
                        case "SYNC" : 
                            sync = true;
                                lp.ajouterLigne(-1, rep[2], Integer.parseInt(rep[1]));
                            break;
                        case "ENDSYNC" :
                            apply();
                            sync = false ;
                            break;
                        default:
                            commandes.add(tmp);
                            break;
                    }
                }

            }catch(IOException e){

            }

            System.out.println("fin ======");
        }
 
    }

    private static int aSupprimer = 0;

    private static void apply(){
        for(String tmp : syncwait){
            String [] rep = tmp.split(":");
            switch (rep[0]) {
                case "UPDATE":
                    lp.update(Integer.parseInt(rep[1]), rep[2]);
                    break; 
                case "CREAT" :
                    lp.ajouterLigne(Integer.parseInt(rep[1]), rep[2], Integer.parseInt(rep[3]));
                    break;
                case "SUPP" :
                    lp.supprimerLigne(Integer.parseInt(rep[1]));
                    break;
                default :
                    break;
            }
            syncwait.remove(tmp);
        }
    }

    private static void suppr(FichierClient lp , String id , Console console){
        System.out.println("Suppression de Ligne \n");
        try{
            int idligne ;
            idligne = Integer.parseInt(id);

            if(! lp.isIn(idligne)){
                System.out.println("Ligne non existante \n"); 
                return ; 
            }

            boolean b = true;
            while( b ){
                String tmp = console.readLine("voulez vous vraiment supprimer la ligne : YES / NO \n");
                switch (tmp.toUpperCase()) {
                    case "YES":
                        b = false;
                        break;
                    case "NO" :
                        return;

                    default:
                        System.out.println("reponse invalide \n"); 
                        break;
                }
            }

            //envoyer req de demande de suppression au serveur
            //si refus : System.out.println("erreur est survenue \n"); return ; 
            //si accepte : 
            lp.supprimerLigne(idligne);

        }catch(NumberFormatException e ){
            System.out.println("Argument n'est pas un nombre \n");
            return;
        }
                
    }

    private static void mod(FichierClient lp , String id , Console console){
        System.out.println("Modification de Ligne \n");
        try{
            int idligne ;
            idligne = Integer.parseInt(id);

            if(! lp.isIn(idligne)){
                System.out.println("Ligne non existante \n"); 
                return ; 
            }

            //envoyer req de demande de modif au serveur
            //si refus : System.out.println("erreur est survenue \n"); return ; 

            //si accepte : 
            String tmp = console.readLine("entrer contenu de la ligne \n");

            //envoyer nouvelle idligne + tmp au serveur 

            lp.modifierLigne(idligne, tmp);

        }catch(NumberFormatException e ){
            System.out.println("Argument n'est pas un nombre \n");
            return;
        }
        
   
    }

    private static void add(FichierClient lp , String id , Console console ,PrintWriter out ){
        System.out.println("Insertion/Creation de ligne \n");
        try{
            String tmp = console.readLine("entrer contenu de la ligne \n");

            int idprec = 0;
            idprec = Integer.parseInt(id);

            out.println("ADD:"+ id + ":" + tmp);
            aSupprimer++;

        }catch(NumberFormatException e ){
            System.out.println("Argument n'est pas un nombre \n");
            return;
        }

    }

    private static void listeReq(){
        String [] instruction = new String[7];
        instruction[0] = "mod <id ligne>" ;
        instruction[1] ="add <id ligne precedente >" ;
        instruction[2] ="suppr <id ligne>" ;
        instruction[3] = "print <id ligne>" ;
        instruction[4] ="printall" ;
        instruction[5] ="exit" ;
        instruction[6] ="help" ;

        for(String s : instruction){
            System.out.println(s);
        }
        
    }

    private static void lancement(){
        
    }
}