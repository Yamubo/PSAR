package Fichier;

public class Ligne{
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

    public boolean locking(){
        if (!this.lock) {
            this.lock = true;
           
            return true;
        }
        return false;
    }

    public void unlocking(){
        this.lock = false;
        
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
    
    public void update(String content ){
        this.contenu = content;
    }
}


