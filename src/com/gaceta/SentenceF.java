package com.gaceta;

import java.util.ArrayList;
import java.util.List;

import edu.upc.freeling.Sentence;
import edu.upc.freeling.VectorWord;


public class SentenceF {
    List<TokenF> tokens = new ArrayList<TokenF>();
    Integer nseq; //sequence in text
    
    public List<TokenF> getTokens() {
        return tokens;
    }

    public Integer getId() {
        return nseq;
    }
    
    public SentenceF(Sentence freelingSt, Integer nseq){
        this.nseq = nseq;
        VectorWord vw = freelingSt.getWords();
        for (int i=0;i<vw.size();i++)
            tokens.add(new TokenF(this, vw.get(i), i));
    }
    
    public List<TokenF> getEntities(){
        List<TokenF> entities = new ArrayList<TokenF>();
        for (TokenF tok: getTokens()){
            if (tok.isEntity())
                entities.add(tok);
        }
        return entities;
    }
}
