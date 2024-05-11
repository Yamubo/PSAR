import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

public class FichierClient extends Fichier{

    public FichierClient(){
        this.ensemble = new ArrayList<>();
    }

    public FichierClient(List<Ligne> ensemble){
        this.ensemble = new ArrayList<>();
        this.ensemble.addAll(ensemble);
    }

    public FichierClient(int[] id , String [] content){
        this.ensemble = new ArrayList<>();
        for(int i = 0 ; i < id.length ; i++){
            ensemble.add(new Ligne(id[i],content[i]));
        }
    }
    public boolean update(int id, String newContent){
        for (Ligne li : this.ensemble) {
            if(li.getId() == id ){
                li.update((newContent));
                return true;
            }
        }
        return false ;
    }
    public boolean updateAll(int[] id , String[] newContent){
        if(id.length != newContent.length){
            return  false ;
        }
        boolean reussi = true;
        for(int i = 0 ; i < id.length ; i++){
            reussi = reussi && update(id[i],newContent[i]);
        }
        return reussi;
    }




}
