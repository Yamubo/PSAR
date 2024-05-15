package Client ;
import java.io.*;
import Fichier.FichierClient;



public class tmp {
    public static int aSupprimer = 0;

    public static void suppr(FichierClient lp , String id , Console console){
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
                switch (tmp) {
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

    public static void mod(FichierClient lp , String id , Console console){
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

    public static void add(FichierClient lp , String id , Console console){
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

    public static void listeReq(){
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

    public static void lancement(){
        
        FichierClient lp  = new FichierClient();

        Console console;
        if ((console = System.console()) == null) {
            System.exit( 1 );
        }

        listeReq();

        boolean fin = true;
        while (fin) {
            String ligne = console.readLine("Quel est votre requête ? \n");
            String [] req = ligne.split(" "); 
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
    public static void main( String args[] ) {
                
        lancement();

    }
}
