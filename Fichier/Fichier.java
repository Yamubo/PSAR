package Fichier;
import java.util.ArrayList;
import java.util.List;

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

    public Integer[] getAllId(){
        List<Integer> ids = new ArrayList<>();

        for(Ligne li : ensemble){
            ids.add(li.getId());
        }

        return (Integer [])ids.toArray();
    }

    public String [] getContent() {
        List<String> content = new ArrayList<>();

        for (Ligne li : ensemble) {
            content.add(li.getContenu());
        }

        return (String [])content.toArray();
    }

    public void print(int idl){
        for(Ligne li : ensemble){
            if(li.getId() == idl){
                System.out.println(idl + " " + li.getContenu() );
            }
        }

    }

    public void printAll(){
        for(Ligne li : ensemble){
            System.out.println(li.getId() + " " + li.getContenu() );
        }
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

    public boolean supprimerLigne(int idLigne){
        int index = findIndex(idLigne);
        if(index > -1){
            if(ensemble.get(index).isLock()){
                return false;
            } else {
                this.ensemble.remove(index);
                return true;
            }
        }
        return false;
    }

    public boolean isIn(int idLigne){
        return findIndex(idLigne) > -1;
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
