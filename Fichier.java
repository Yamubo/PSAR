import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

//rendre la création,mo
public class Fichier {
    
    private class Ligne{
        private boolean lock = false;
        private int idl;
        private String contenu = null;

        public Ligne(int id){
            this.idl = id;
        }

        public Ligne(int id , String str){
            this.idl = id;
            this.contenu = str;
        }

        public void modifier(String str){
            this.contenu = str;
        }

        public boolean locking(String user){
            if (!this.lock) {
                this.lock = true;
                locks.put( idl, user);
                return true;
            }
            return false;
        }

        public void unlocking(){
            this.lock = false;
            locks.remove(this.idl);
        }

        public boolean isLock(){
            return this.lock;
        }

        public int getId(){
            return this.idl;
        }

        public String getContenu(){
            return this.contenu;
        }
    }

    private static int id = 0;
    private List<Ligne> ensemble;
    private Map<Integer , String> locks;

    public Fichier() {
        this.ensemble = new ArrayList<>();
        this.locks = new HashMap<>();
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

    //ajoute un lock à la ligne
    public boolean addLock(int id, String user){
        for (Ligne li : ensemble) {
            if (li.getId() == id) {
                return li.locking(user);
            }
        }

        return false;
    }

    //unlock la ligne et retire le lock
    //false si la ligne n'existe pas , True si la ligne à bien été Unlock
    public boolean unlock(int id , String user){

        for (Ligne li : ensemble) {
            if (li.getId() == id) {
                li.unlocking();
                return true;
            }
        }

        return false;
    }

    private int findIndex(int id){

        for(Ligne li : ensemble){
            if(li.getId() == id){
                return ensemble.indexOf(li);
            }
        }

        return -1;
    }

    //insere un ligne après la ligne passée en id
    //retourne l'id de la nouvelle ligne créée
    public int ajouterLigne(int id, String contenu){
        int newId = ++this.id; // Incrémentation du compteur pour générer un nouvel ID

        if (id == -1 || ensemble.isEmpty()) {
            ensemble.add(0, new Ligne(newId, contenu)); // Ajout au début de la liste
            return newId;
        } 
        
        int index = this.findIndex(id);
        if( index == -1){
            ensemble.add(new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }else {
            ensemble.add(index,new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }

        return newId;
    }

    public int maxNumber(){
        return this.id;
    }

    public List<String> getContent() {
        List<String> content = new ArrayList<>();

        for (Ligne li : ensemble) {
            content.add(li.getContenu());
        }

        return content;
    }
    /* 
    public static void main(String[] args){

        Fichier f = new Fichier();
        
        f.ajouterLigne(f.maxNumber(),"Lets go");
        f.ajouterLigne(0,"party ! ");

        List<String> content = f.getContent();
        for( String li : content){
            System.out.println(li);
        }
        
        f.addLock(1,"visir");
        f.modifierLigne(1,"ok","terie");
        f.unlock(1,"visir");

        System.out.println("===========");
        content = f.getContent();
        for( String li : content){
            System.out.println(li);
        }

        f.modifierLigne(1,"ok","visir");

        System.out.println("===========");
        content = f.getContent();
        for( String li : content){
            System.out.println(li);
        }

    
    } */
}

