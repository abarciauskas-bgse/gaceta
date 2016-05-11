package com.gaceta;

import java.util.ArrayList;

/**
 * Created by abarciauskas on 5/11/16.
 */
public class Alignment {
    static int gapPenalty = -1;
    // needlemanWunsch computes global alignment between 2 sequences
    public static int needlemanWunsch(ArrayList<String> doc1, ArrayList<String> doc2) {
        // first construct an array of arrays
        int[][] scoreMatrix = new int[doc1.size()][doc2.size()];

        // the first row and column should be filled with -1*index
        for (int j = 0; j < doc2.size(); j ++) {
            scoreMatrix[0][j] = -1*j;
        }
        for (int i = 0; i < doc1.size(); i++) {
            scoreMatrix[i][0] = -1*i;
        }
        // compute scores
        int maxscoreMatrix = 0;
        for (int i = 1; i < doc1.size(); i++) {
            for (int j = 1; j < doc2.size(); j++) {
                int simij = similarity(doc1.get(i), doc2.get(j));
                int qdiag = scoreMatrix[i-1][j-1] + simij;
                int qup = scoreMatrix[i-1][j] + gapPenalty;
                int qdown = scoreMatrix[i][j-1] + gapPenalty;
                int[] scores = {simij, qdiag, qup, qdown};
                int maxscore = Utils.arraymax(scores);
                if (maxscoreMatrix < maxscore) {
                    maxscoreMatrix = maxscore;
                };
                scoreMatrix[i][j] = maxscore;
            }
        }
        //return scoreMatrix;
        return maxscoreMatrix;
    }

    public static int similarity(String char1, String char2) {
        return char1.equals(char2) ? 1 : 0;
    }
}
