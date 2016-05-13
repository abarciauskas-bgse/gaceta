package com.gaceta;

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
}
