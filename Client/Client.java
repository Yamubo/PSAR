package Client;

import Fichier.FichierClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.Console;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static String username;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Client connecté au serveur !");

            while (true) {
                displayMenu();
                String choice = reader.readLine().trim().toLowerCase();

                switch (choice) {
                    case "1":
                        createFile(out, in, reader);
                        break;
                    case "2":
                        listFiles(out, in, reader);
                        break;
                    case "3":
                        writeFile(out, in, reader);
                        break;
                    case "4":
                        deleteFile(out, in, reader);
                        break;
                    case "5":
                        return;
                    default:
                        System.out.println("Le choix est invalide ! Veuillez réessayer.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Créer un fichier");
        System.out.println("2. Lister les fichiers");
        System.out.println("3. Écrire dans un fichier");
        System.out.println("4. Supprimer un fichier");
        System.out.println("5. Quitter");
        System.out.print("Entrer votre choix : ");
    }

    private static void createFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();

        String coherencePolicy = "UPDATE";

        out.println("CREATE:" + fileName + ":" + coherencePolicy);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void listFiles(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        out.println("LIST");
        String line = in.readLine();
        if (line.equals("NO_FILES")) {
            System.out.println("Aucun fichier disponible.");

            return;
        }

        System.out.println("Liste des fichiers disponibles :");

        while (!line.equals("END")) {
            System.out.println(line);
            line = in.readLine();
        }

        // Vérifier si aucun fichier n'est disponible
        if (!line.equals("END")) {
            // S'il y a des fichiers disponibles, demander le nom du fichier à lire
            System.out.print("Entrer le nom du fichier à lire (ou appuyez sur Entrée pour revenir au menu) : ");
            String fileName = reader.readLine();

            if (!fileName.isEmpty()) {
                out.println("READ:" + fileName);
                line = in.readLine();
                while (!line.equals("END")) {
                    System.out.println(line);
                    line = in.readLine();
                }
            } else {
                // Retourner au menu principal si aucun nom de fichier n'est entré
                displayMenu(); // Afficher à nouveau le menu
            }
        }
    }


    private static void writeFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();

        System.out.print("Saisir le texte à écrire : ");
        String content = reader.readLine();

        out.println("WRITE:" + fileName + ":" + content);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void deleteFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();
        out.println("DELETE:" + fileName);
        String response = in.readLine();
        System.out.println(response);
    }

    private static int aSupprimer = 0;

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

    private static void add(FichierClient lp , String id , Console console){
        System.out.println("Insertion/Creation de ligne \n");
        try{
            String tmp = console.readLine("entrer contenu de la ligne \n");

            int idprec = 0;
            int idnew = 0;
            idprec = Integer.parseInt(id);

                                   
            //envoyer la req au serveur // idprec et tmp
            //attendre de recevoir le nouvel id de la ligne

            lp.ajouterLigne(idprec, tmp, aSupprimer);
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
        
        FichierClient lp  = new FichierClient();

        Console console;
        if ((console = System.console()) == null) {
            System.exit( 1 );
        }

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
                            add(lp, req[1], console);
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
    }
}