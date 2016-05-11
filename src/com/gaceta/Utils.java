package com.gaceta;

/**
 * Created by abarciauskas on 5/11/16.
 */
public class Utils {
    public static int arraymax(int[] array) {
        int max = 0;

        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}
