package com.gaceta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by abarciauskas on 5/11/16.
 */
public class Utils {
    // FIXME: Should instantiate max as null and revise conditions
    public static int arraymax(int[] array) {
        int max = -9999999;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static String databaseArrayString(List<String> objectsArray) {
        String arrayString = "";

            for (String s : objectsArray) {
                arrayString += "\"" + s.replace("\"", "'").replace("'", "''") + "\",";
            }
            arrayString = arrayString.substring(0, arrayString.length() - 1);
            arrayString = "{" + arrayString + "}";
        return arrayString;
    }

    public static String databaseArrayDoubleString(Double[] objectsArray) {
        String arrayString = "";
        for (Double v : objectsArray) {
            arrayString += v + ",";
        }
        arrayString = arrayString.substring(0, arrayString.length() - 1);
        arrayString = "{" + arrayString + "}";
        return arrayString;
    }

    public static double cosineSimilarity(BigDecimal[] vectorA, BigDecimal[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i].doubleValue() * vectorB[i].doubleValue();
            normA += Math.pow(vectorA[i].doubleValue(), 2);
            normB += Math.pow(vectorB[i].doubleValue(), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
