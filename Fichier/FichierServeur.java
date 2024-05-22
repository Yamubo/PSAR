package Fichier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FichierServeur extends Fichier {
    private static int id = 0;
    private Map<Integer , String> locks;

    public FichierServeur(){
        this.ensemble = new ArrayList<>();
        locks = new HashMap<>() ;
    }

    //ajoute un lock à la ligne
    public synchronized boolean addLock(int id, String user){
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
    public synchronized boolean modifierLigne(int id , String contenu, String user){
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

    public synchronized boolean isUnlock_and_exist(int idligne , String user){
        if(! isIn(idligne)) return false;
        String tmp = locks.get(idligne);
        if(tmp != null && ! tmp.equals(user) ){
            return false ;
        }
        
        return true;
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
        @SuppressWarnings("static-access")
        int newId = ++this.id; // Incrémentation du compteur pour générer un nouvel ID
        

        if (idprec == -1 || this.ensemble.isEmpty()) {
            this.ensemble.add(0, new Ligne(newId, contenu)); // Ajout au début de la liste
            return newId;
        } 
        
        int index = this.findIndex(idprec);
        if( index == -1 || (index+1) >= ensemble.size()){
            this.ensemble.add(new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }else {
            this.ensemble.add(index+1,new Ligne(newId, contenu)); 
        }

        return newId;
    }

    public synchronized boolean supprimerLigne(int id , String user){
        int index = findIndex(id);
        if(index > -1){

            String tmp = locks.get(id);
            if(tmp != null && ! tmp.equals(user)){
                return false;
            }

            //if(ensemble.get(index).isLock() ){
            //    return false; }
            else {
                this.ensemble.remove(index);
                this.locks.remove(id);
                return true;
            }
        }
        return false;
    }


}
