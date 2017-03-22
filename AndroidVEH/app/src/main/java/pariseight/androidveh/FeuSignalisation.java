package pariseight.androidveh;

import java.util.List;

/**
 * Created by Guillaume on 11/03/2017.
 */

public class FeuSignalisation {

    public int ROUGE;
    public int VERT;
    public int BLEU;

    public int getRouge(){
        return this.ROUGE;
    }
    public void setRouge(int rouge){
        this.ROUGE = rouge - 128;
    }
    public int getVert(){
        return this.VERT;
    }
    public void setVert(int vert){
        this.VERT = vert - 128;
    }
    public int getBleu(){
        return this.BLEU;
    }
    public void setBleu(int bleu){
        this.BLEU = bleu - 128;
    }
}