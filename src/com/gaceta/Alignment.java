package com.gaceta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.sql.*;

/**
 * Created by abarciauskas on 5/11/16.
 */
public class Alignment {
    //  chosen arbitrarily
    static int gapPenalty = -2;
    // needlemanWunsch computes global alignment between 2 sequences
    // http://www.slideshare.net/avrilcoghlan/the-needleman-wunsch-algorithm
    // https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm
    // FIXME: Add conditional to print matrix.
    public static HashMap needlemanWunsch(String[] doc1, String[] doc2, boolean printMatrix) {
        printMatrix = printMatrix ? printMatrix : false;
        // first construct an array of arrays
        int[][] scoreMatrix = new int[doc1.length+1][doc2.length + 1];

        // the first row and column should be filled with gapPenalty*index
        for (int j = 0; j < doc2.length; j ++) {
            scoreMatrix[0][j] = gapPenalty*(j);
        }
        for (int i = 0; i < doc1.length; i++) {
            scoreMatrix[i][0] = gapPenalty*(i);
        }
        if (printMatrix) {
            for (int sc : scoreMatrix[0]) {
                System.out.print(sc + ", ");
            }
            System.out.println();
        }
        // compute scores
        int finalScore;
        for (int i = 1; i <= doc1.length; i++) {
            if (printMatrix) System.out.print(scoreMatrix[i][0] + ", ");
            for (int j = 1; j <= doc2.length; j++) {
                int simij = similarity(doc1[i-1], doc2[j-1]);
                int qdiag = scoreMatrix[i-1][j-1] + simij;
                int qup = scoreMatrix[i-1][j] + gapPenalty;
                int qdown = scoreMatrix[i][j-1] + gapPenalty;
                int[] scores = {qdiag, qup, qdown};
                int maxscore = Utils.arraymax(scores);
                scoreMatrix[i][j] = maxscore;
                if (printMatrix) System.out.print(maxscore + ", ");
            }
            if (printMatrix) System.out.println();
        }
        finalScore = scoreMatrix[doc1.length-1][doc2.length-1];
        HashMap<String, Object> mapReturn = new HashMap();
        mapReturn.put("score", finalScore);
        mapReturn.put("matrix", scoreMatrix);
        return mapReturn;
    }

    public static int similarity(String char1, String char2) {
        return char1.equals(char2) ? 1 : -1;
    }

    public static void printAlignment(int[][] scoreMatrix, String[] doc1, String[]  doc2) {
        int i = doc1.length;
        int j = doc2.length;
        ArrayList<int[]> positions = new ArrayList<>();
        int[] lastPosition = {i,j};
        positions.add(lastPosition);

        // build a list of positions through the scores matrix
        while (!(i == 0 && j == 0)) {
            // find max next position
            // start with something really low
            // diag, left, up
            HashMap<String, Integer> scores = new HashMap<>();

            if (i > 0 && j > 0) {
                scores.put("diag", scoreMatrix[i-1][j-1]);
            } else {
                scores.put("diag", -999);
            }

            if (i >= 0 && j > 0) {
                scores.put("left", scoreMatrix[i][j-1]);
            } else {
                scores.put("left", -999);
            }

            if (i > 0 && j >= 0) {
                scores.put("up", scoreMatrix[i-1][j]);
            } else {
                scores.put("up", -999);
            }
            // FIXME: Hack should not be anything
            String maxKey = "left";
            int maxScore = -999;

            Iterator it = scores.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                int thisVal = (int) pair.getValue();
                if (thisVal > maxScore) {
                    maxScore = thisVal;
                    maxKey = (String) pair.getKey();
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            switch (maxKey) {
                case "diag":
                    i = (i > 0) ? i - 1 : i;
                    j = (j > 0) ? j - 1 : j;
                    break;
                case "up":
                    i = (i > 0) ? i - 1 : i;
                    break;
                case "left":
                    j = (j > 0) ? j - 1 : j;
                    break;
            }
            int[] nextPosition = {i, j};
            positions.add(nextPosition);
        } // end positions loop

        // now print positions
        ArrayList<String> align1 = new ArrayList<>();
        ArrayList<String> align2 = new ArrayList<>();
        for (int posi = positions.size()-1; posi > 0; posi--) {
            int[] currentPosition = positions.get(posi);
            int[] nextPosition = positions.get(posi - 1);
            // moved right
            if (nextPosition[0] == currentPosition[0] && nextPosition[1] != currentPosition[1]) {
                align1.add("--");
                align2.add(doc2[currentPosition[1]]);
            // moved down
            } else if (nextPosition[1] == currentPosition[1] && nextPosition[0] != currentPosition[0]) {
                align1.add(doc1[currentPosition[0]]);
                align2.add("--");
            } else {
                // if score of next position is lower than current position, then this is a mismatch
                if (scoreMatrix[nextPosition[0]][nextPosition[1]] < scoreMatrix[currentPosition[0]][currentPosition[1]]) {
                    align1.add("|");
                    align2.add("|");
                } else {
                    align1.add(doc1[currentPosition[0]]);
                    align2.add(doc2[currentPosition[1]]);
                }
            }
        }

        System.out.println("Alignment: ");
        System.out.println(align1);
        System.out.println(align2);
    }
}
