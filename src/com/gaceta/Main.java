package com.gaceta;

import java.util.*;
import edu.upc.freeling.*;

public class Main {
    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String LANG = "ca";

    public static void main(String[] args) {
        System.load("/Users/aimeebarciauskas/Projects/freeling_src/APIs/java/libfreeling_javaAPI.dylib");
//
        Util.initLocale( "default" );

        // Create options set for maco analyzer.
        // Default values are Ok, except for data files.
        MacoOptions op = new MacoOptions( LANG );

        op.setDataFiles( "",
                DATA + "common/punct.dat",
                DATA + LANG + "/dicc.src",
                DATA + LANG + "/afixos.dat",
                "",
                DATA + LANG + "/locucions.dat",
                DATA + LANG + "/np.dat",
                DATA + LANG + "/quantities.dat",
                DATA + LANG + "/probabilitats.dat");

        try {
            String filename;
            filename = "/Users/aimeebarciauskas/GACETA/SPLIT_NORM/29-2002_18.txt";
            //filename = "/Users/aimeebarciauskas/Desktop/test.txt";
            Corpus corpus;
            corpus = new Corpus(filename);
            Alignment alignment = new Alignment();

            for (int i = 0; i < corpus.documents.size(); i++) {
                for (int j = 0; j < corpus.documents.size(); j++) {
                    if (i < j) {
                        ArrayList doc1 = corpus.documents.get(i);

                        ArrayList doc2 = corpus.documents.get(j);
                        HashMap nw = alignment.needlemanWunsch(doc1, doc2, false);
                        int nwscore = (int) nw.get("score");
                        int[][] nwmatrix = (int[][]) nw.get("matrix");
                        if (nwscore > -999) {
                            Alignment.printAlignment(nwmatrix, doc1, doc2);
                            System.out.println("Score: " + nwscore);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }
}

