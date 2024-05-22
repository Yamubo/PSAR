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

        if(ids.size() == 0) return null;

        return ids.toArray(new Integer[0]);
    }

    public String [] getContent() {
        ArrayList<String> content = new ArrayList<>();

        for (Ligne li : ensemble) {
            content.add(li.getContenu());
        }

        if(content.size() == 0) return null;

        return content.toArray(new String[0]);
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

        int index = this.findIndex(idprec) ;
        if( index == -1 || (index+1) >= ensemble.size()){
            this.ensemble.add(new Ligne(newId, contenu)); // Ajout à la fin de la liste
        }else {
            this.ensemble.add(index+1,new Ligne(newId, contenu)); 
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

    public String getSpecContent(int idl){
        int index = findIndex(idl);
        if(index == -1)return null;
        return ensemble.get(index).getContenu();
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
