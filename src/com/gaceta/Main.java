package com.gaceta;

import java.io.File;
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
            String filetype = "SPLIT_NORM";
            final java.io.File folder = new java.io.File("/Users/aimeebarciauskas/GACETA/" + filetype);
            java.io.PrintWriter writer = new java.io.PrintWriter("nw_" + filetype.toLowerCase() + "_times.csv", "UTF-8");

            int limit = 0;

            for (final File fileEntry : folder.listFiles()) {
                limit++;
                if (limit > 1) {
                    break;
                } else {
                    System.out.println("Reading file: " + fileEntry.getName());
                    final long startTime = System.currentTimeMillis();
                    Corpus corpus;
                    corpus = new Corpus(fileEntry.getAbsolutePath());
                    Alignment alignment = new Alignment();

                    for (int i = 0; i < corpus.documents.size(); i++) {
                        for (int j = 0; j < corpus.documents.size(); j++) {
                            if (i < j) {
                                ArrayList doc1 = corpus.documents.get(i);
                                ArrayList doc2 = corpus.documents.get(j);
                                if (doc1.size() > 0 && doc2.size() > 0) {
                                    HashMap nw = alignment.needlemanWunsch(doc1, doc2, false);
                                    int nwscore = (int) nw.get("score");
                                    int[][] nwmatrix = (int[][]) nw.get("matrix");
                                    Alignment.printAlignment(nwmatrix, doc1, doc2);
                                }
                            }
                        }
                    }
                    final long endTime = System.currentTimeMillis();
                    writer.println(fileEntry.getName() + ", " + fileEntry.length() + ", " + (endTime - startTime));
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }
}

