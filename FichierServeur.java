import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

public class FichierServeur extends Fichier {
    private static int id = 0;
    private Map<Integer , String> locks;

    public FichierServeur(){
        this.ensemble = new ArrayList<>();
        locks = new HashMap<>() ;
    }

    //ajoute un lock à la ligne
    public boolean addLock(int id, String user){
        for (Ligne li : this.ensemble) {
            if (li.getId() == id) {
                locks.put( id, user);
                return li.locking();
            }
        }

        return false;
    }

    //modifie la ligne avec un id précis
    //retourne l'etat de réussite de la modification
    public boolean modifierLigne(int id , String contenu, String user){
        for (Ligne li : this.ensemble) {
            if (li.getId() == id) {

                boolean lo = true;
                String tmp = locks.get(id);
                if(tmp != null && ! tmp.equals(user)){
                    lo = false;
                }

                if (li.isLock() && ! lo) {
                    return false;
                }

                li.modifier(contenu);
                return true;
            }
        }
        //possibilité de rajouter une erreur
        return false;
    }


    //unlock la ligne et retire le lock
    //false si la ligne n'existe pas , True si la ligne à bien été Unlock
    public boolean unlock(int id , String user){

        for (Ligne li : this.ensemble) {
            if (li.getId() == id) {
                li.unlocking();
                locks.remove(id);
                return true;
            }
        }

        return false;
    }

    //insere un ligne après la ligne passée en id
    //retourne l'id de la nouvelle ligne créée
    public int ajouterLigne(int idprec, String contenu){
        int newId = ++this.id; // Incrémentation du compteur pour générer un nouvel ID

        if (idprec == -1 || this.ensemble.isEmpty()) {
            this.ensemble.add(0, new Ligne(newId, contenu)); // Ajout au début de la liste
            return newId;
        } 
        
        int index = this.findIndex(idprec);
        if( index == -1){
            this.ensemble.add(new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }else {
            this.ensemble.add(index,new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }

        return newId;
    }

    public int maxNumber(){
        return this.id;
    }

}
