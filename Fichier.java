import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//rendre la création,mo
/*public class Fichier {
    private static int id = 0;
    private List<Ligne> ensemble;
    private Map<String, Integer> locks;

    public Fichier() {
        this.ensemble = new ArrayList<>();
        this.locks = new HashMap<>();
    }

    private class Ligne{
        private boolean lock = false;
        private int id;
        private String contenu = null;


        public Ligne(int id){
            this.id = id;
        }

        public Ligne(int id , String str){
            this.id = id;
            this.contenu = str;
        }

        public void modifierLigne(String str){
            this.contenu = str;
        }

        public boolean locking(){
            this.lock = true;
            return lock;
        }

        public void unlocking(){
            this.lock = false;
        }

        public boolean isLock(){
            return this.lock;
        }

        public int getId(){
            return this.id;
        }

        public String getContenu(){
            return this.contenu;
        }
    }

    //modifie la ligne avec un id précis
    //retourne l'etat de réussite de la modification
    public boolean modifierLigne(int id , String contenu ){
        for (Ligne li : ensemble) {
            if (li.getId() == id) {
                li.modifierLigne(contenu);
                return true;
            }
        }
        //possibilité de rajouter une erreur
        return false;
    }

    //ajoute un lock à la ligne
    //ajoute un lock
    public boolean addLock(int id,String user){
        if (locks.containsKey(id)) {
            return true;
        }

        for (Ligne li : ensemble) {
            if (li.getId()==id) {
                if (li.isLock()) {
                    return true;
                } else {
                    li.locking();
                    locks.add(id,user);
                    return true;
                }
            }
        }
        return false;
    }

    //unlock la ligne et retire le locks
    public void unlock(int id){
        if (! locks.containsKey(id)) {
            return;
        }

        for (Ligne li : ensemble) {
            if(li.getId()==id){
                li.unlocking();
                locks.remove(id);
            }
        }
    }

    //insere un ligne après la ligne passée en id
    //retourne l'id de la nouvelle ligne créée
    public int ajouterLigne(int id, String li, boolean deb ){
        int newId = ++id; // Incrémentation du compteur pour générer un nouvel ID

        if (deb) {
            ensemble.add(0, new Ligne(newId, contenu)); // Ajout au début de la liste
        } else {
            ensemble.add(new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }

        return newId;
    }
}*/