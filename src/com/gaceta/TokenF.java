package com.gaceta;

import edu.upc.freeling.Word;


public class TokenF {
    SentenceF st;    
    String str;
    String lemma;
    Integer nseq; //sequence in sentence
    String POS;
    //N 2 postype/C:common;P:proper gen/F:f;M:m;C:c num/S:s;P:p;N:c neclass/S:person;G:location;O:organization;V:other nesubclass/0:0;P:0 grade/A:augmentative;D:diminutive
    //V 3 postype/M:main;A:auxiliary;S:semiauxiliary mood/I:indicative;S:subjunctive;M:imperative;P:participle;G:gerund;N:infinitive
    //D 2 postype/A:article;D:demonstrative;E:exclamative;I:indefinite;T:interrogative;N:numeral;P:possessive;R:relative person/1:1;2:2;3:3 gen/F:f;M:m;C:c;N:c num/S:s;P:p;N:c possessornum/S:s;P:p
    //P 2 postype/D:demonstrative;E:exclamative;I:indefinite;T:interrogative;N:numeral;P:personal;X:possessive;R:relative person/1:1;2:2;3:3 gen/F:f;M:m;C:c;N:c num/S:s;P:p;N:c case/N:nominative;A:accusative;D:dative;O:oblique possessornum/S:s;P:p;C:c polite/P:yes
    //S 2 postype/P:preposition contracted/S:0;C:yes gen/M:m num/S:s;P:p


    public String getStr() {
        return str;
    }
    
    public Integer getNseq() {
        return nseq;
    }

    void setStr(Word freelingW) {
        try{
            this.str = freelingW.getForm();
        }catch(Exception e){
            this.str = null;
        }
    }

    public String getLemma() {
        return lemma;
    }

    void setLemma(Word freelingW) {
        try{
            this.lemma = freelingW.getLemma();
        }catch(Exception e){
            this.lemma = null;
        }
    }

    public String getPOS() {
        return POS;
    }

    void setPOS(Word freelingW) {
        try{
            this.POS = freelingW.getTag();
        }catch(Exception e){
            this.POS = null;
        }
    }

    public SentenceF getSt() {
        return st;
    }

    void setSt(SentenceF st) {
        this.st = st;
    }

    public TokenF(SentenceF st, Word freelingW, Integer nseq){
        setStr(freelingW);
        setLemma(freelingW);
        setPOS(freelingW);
        this.nseq = nseq;
    }
    
    
    public boolean isProperNoun(){
        if (this.POS != null){
            return (POS.startsWith("NP"));
        }
        return false;
    }
    
    public boolean isDate(){
        if (this.POS != null){
            return (this.POS.startsWith("W"));
        }
        return false;
    }
    
    public boolean isQuantity(){
        if (this.POS != null){
            return (this.POS.startsWith("Z"));
        }
        return false;
    }
    
    public boolean isPerson(){
        if (this.POS != null){
            return (POS.charAt(4) =='S');
        }
        return false;
    }
    
    public boolean isLocation(){
        if (this.POS != null){
            return (POS.charAt(4) =='G');
        }
        return false;
    }
    
    public boolean isOrganization(){
        if (this.POS != null){
            return (POS.charAt(4) =='O');
        }
        return false;
    }
    
    public boolean isMiscellanea(){
        if (this.POS != null){
            return (POS.charAt(4) =='V');
        }
        return false;
    }
    
    public boolean isEntity(){
        if (this.POS != null){
            return (isProperNoun() || isDate() || isQuantity());
        }
        return false;
    }

    public boolean isVerb(){
        if (this.POS != null)
            return (POS.startsWith("V"));
        return false;
    }
    
    public boolean isPronoun(){
        if (this.POS != null)
            return (POS.startsWith("P"));
        return false;
    }
    
    public boolean isDeterminant(){
        if (this.POS != null)
            return (POS.startsWith("D"));
        return false;
    }
    
    public boolean isProposition(){
        if (this.POS != null)
            return (POS.startsWith("S"));
        return false;
    }
    
    public boolean isNoun(){
        if (this.POS != null)
            return (POS.startsWith("N"));
        return false;
    }
    
}
