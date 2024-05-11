import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

//rendre la création,mo
public class Fichier {
    protected List<Ligne> ensemble;

    public Fichier() {
        this.ensemble = new ArrayList<>();
    }

    protected int findIndex(int id){
        for(Ligne li : ensemble){
            if(li.getId() == id){
                return ensemble.indexOf(li);
            }
        }

        return -1;
    }

    public List<String> getContent() {
        List<String> content = new ArrayList<>();

        for (Ligne li : ensemble) {
            content.add(li.getContenu());
        }

        return content;
    }

    public boolean modifierLigne(int id , String contenu){
        for (Ligne li : this.ensemble) {
            if (li.getId() == id) {
                li.modifier(contenu);
                return true;
            }
        }
        //possibilité de rajouter une erreur
        return false;
    }

    public void ajouterLigne(int idprec, String contenu , int newId){
        if (idprec == -1 || this.ensemble.isEmpty()) {
            this.ensemble.add(0, new Ligne(newId, contenu)); // Ajout au début de la liste
            return ;
        }

        int index = this.findIndex(idprec);
        if( index == -1){
            this.ensemble.add(new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }else {
            this.ensemble.add(index,new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }

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
