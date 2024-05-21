package Client;

import Fichier.FichierClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static String username;
    private static List<String> syncwait = new ArrayList<String>();
    private static String commandes = null;
    private static FichierClient lp  = new FichierClient();
    //private static int aSupprimer = 0;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            Reception tm = new Reception(in);
            new Thread(tm).start();
            System.out.println("Client connecté au serveur !");

            /*Console console;
            if ((console = System.console()) == null) {
                System.exit( 1 );
            }

            username = reader.readLine("entrer un username : \n");*/

            System.out.print("Entrer un username : ");
            username = reader.readLine();

            out.println("USER:" + username);
            out.println("SYNC:0");
            listeReq();

            boolean fin = true;
            while (fin) {
                //String ligne = console.readLine("Quel est votre requête ? \n");

                System.out.print("Quel est votre requête ? ");
                String ligne = reader.readLine();

                String [] req = ligne.toLowerCase().split(" ");
                if(req.length >= 1 ){
                    switch (req[0]) {
                        case "mod":
                            if(req.length > 1)
                                mod(lp, req[1], reader, out);
                            else
                                System.out.println("mauvais nombre d'arguments \n");
                            break;
                        case "suppr":
                            if(req.length > 1)
                                suppr(lp, req[1], reader, out);
                            else
                                System.out.println("mauvais nombre d'arguments \n");
                            break;

                        case "add":
                            if(req.length == 2)
                                add(lp, req[1], reader, out);
                            else if (req.length == 1){
                                add(lp, null, reader, out);
                            }else
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

            //cette partie permet d'éliminer l'erreur "connection reset du serveur" mais ne termine pas l'exécution du code client
            /*reader.close();
            in.close();
            out.close();
            socket.close();*/

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
                            commandes = tmp;
                            break;
                    }
                }

            }catch(IOException e){
                e.printStackTrace();
            }

            System.out.println("fin ======");
        }

    }

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

    private static void suppr(FichierClient lp , String id , BufferedReader reader, PrintWriter out) throws IOException {
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
                //String tmp = console.readLine("voulez vous vraiment supprimer la ligne : YES / NO \n");
                System.out.print("voulez vous vraiment supprimer la ligne : YES / NO \n");
                String tmp = reader.readLine();

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
            //lp.supprimerLigne(idligne);

            System.out.println("Demande de suppression de la ligne: " + idligne);
            out.println("SUPP:" + idligne);

            while(commandes == null){
                continue;
            }

            String [] tmp = commandes.split(":");
            if(tmp[0].equals("REFUS")){
                System.out.println("REFUS de suppression");
                return;
            }
            if(tmp[0].equals("ACCEPT")){
                System.out.println("Ligne supprimer sur le serveur");
            }else{
                System.out.println("Erreur lors de la suppression");
            }

        }catch(NumberFormatException e ){
            System.out.println("Argument n'est pas un nombre \n");
            return;
        }

    }

    private static void mod(FichierClient lp , String id , BufferedReader reader, PrintWriter out) throws IOException {
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
            System.out.println("Demande de modification de la ligne: " + idligne);
            out.println("MODASK:" + idligne);

            while(commandes == null){
                continue;
            }

            String [] tmp = commandes.split(":");
            if(tmp[0].equals("REFUS")){
                System.out.println("REFUS de modification");
                return;
            }
            if(! tmp[0].equals("ACCEPT")){
                System.out.println("Erreur lors de la modification");
                return;
            }
            System.out.println("================ ancienne ligne ================");
            if(tmp.length > 1 ){
                System.out.println(tmp[1]);
            }

            //si accepte :
            System.out.println("entrer contenu de la ligne \n");
            String tmq = reader.readLine();

            out.println("MOD:" + idligne + ":" + tmq);
            //envoyer nouvelle idligne + tmp au serveur 

            //lp.modifierLigne(idligne, tmp);

            System.out.println("Nouvelle ligne envoyée \n");

        } catch (NumberFormatException e ){
            System.out.println("Argument n'est pas un nombre \n");
            return;
        }
    }

    private static void add(FichierClient lp , String id , BufferedReader reader, PrintWriter out) throws IOException {
        System.out.println("Insertion/Creation de ligne \n");

        try {
            /*String tmp = console.readLine("entrer contenu de la ligne \n");

            int idprec = 0;
            int idnew = 0;
            idprec = Integer.parseInt(id);

                                   
            //envoyer la req au serveur // idprec et tmp
            //attendre de recevoir le nouvel id de la ligne

            lp.ajouterLigne(idprec, tmp, aSupprimer);
            aSupprimer++;*/

            int idprec = -1;
            if (id != null) {
                idprec = Integer.parseInt(id);
            }

            System.out.println("entrer contenu de la ligne \n");
            String tmp = reader.readLine();

            out.println("ADD:" + idprec + ":" + tmp);

        } catch (NumberFormatException e ){
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
}