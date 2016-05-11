package com.gaceta;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by abarciauskas on 5/11/16.
 */
public class Corpus {
    static ArrayList<ArrayList<String>> documents;
    static HashMap termDictionary;
    static int[][] documentTermMatrix;
    static Double[][] tfIdfMatrix;

    public Corpus (String filename)
    {
        // Open the file that is the first
        // command line parameter
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        documents = readDocuments(br);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList readDocuments(BufferedReader br) {
        String strLine;
        String fullDocument = new String();

        // instantiate list of documents
        ArrayList<ArrayList<String>> documents = new ArrayList<>();
        //Read File Line By Line
        try {
            while ((strLine = br.readLine()) != null) {
                fullDocument = fullDocument + " " + strLine;
            }
            String[] parts;
            parts = fullDocument.split("\\.");

            // for every part in this line (there is likely only 1), remove punctuation and split by space
            for (int i = 0; i < parts.length; i ++) {
                String str = parts[i];
                // remove commas, colons and semi-colons
                // TODO: Remove stop words
                String result = str.replaceAll("[,|;|:]", "");

                // split each part by spaces - list of words for this part
                ArrayList<String> words = new ArrayList<>(Arrays.asList(result.split("\\s")));

                // ignore sentences less than length threshold
                // TODO: ignore non-sentences by requiring object an verb
                if (words.size() > 4) {
                    documents.add(words);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return documents;
    }

    // creates a dictionary of terms {term: index}
    public static HashMap createTermDictionary() {
        termDictionary = new HashMap();
        for (int i = 0; i < documents.size(); i++) {
            ArrayList<String> document = documents.get(i);
            for (int j = 0; j < document.size(); j ++) {
                String word = document.get(j);
                if (!termDictionary.containsKey(word)) {
                    termDictionary.put(word, termDictionary.size());
                }
            }

        }
        return termDictionary;
    }

    // createDocumentTermMatrix takes an array list of documents which contains an array of string lists
    public static int[][] createDocumentTermMatrix() {
        documentTermMatrix = new int[documents.size()][termDictionary.size()];

        for (int i = 0; i < documents.size(); i++) {
            ArrayList document = documents.get(i);
            for (int j = 0; j < document.size(); j++) {
                Object word = document.get(j);
                int termIndex = (int) termDictionary.get(word);
                int currentCount = documentTermMatrix[i][termIndex];
                documentTermMatrix[i][termIndex] = currentCount + 1;
            }

        }
        return documentTermMatrix;
    }

    public static Double[][] createTfIdfMatrix() {
        int N = documentTermMatrix.length;
        tfIdfMatrix = new Double[N][termDictionary.size()];
        HashMap<String, Integer> termFrequencies = new HashMap<>();
        HashMap<String, Double> idfs = new HashMap<>();
        ArrayList terms = new ArrayList(termDictionary.keySet());

        // compute the term frequency of each term v
        for (int v = 0; v < terms.size(); v++) {
            String term = (String) terms.get(v);
            int termFrequency = 0;
            for (int i = 0; i < N; i++) {
                termFrequency += documentTermMatrix[i][v];
            }
            termFrequencies.put(term, termFrequency);
            // compute the idf = Math.log_10(N/term_frequency)
            idfs.put(term, Math.log10(N/termFrequency));
        }

        for (int i = 0; i < N; i++) {
            for (int v = 0; v < terms.size(); v++) {
                int xdv = documentTermMatrix[i][v];
                Double idf = idfs.get(terms.get(v));
                if (xdv > 0 && idf > 0) {
                    tfIdfMatrix[i][v] = (1 + Math.log10(xdv))*idf;
                } else {
                    tfIdfMatrix[i][v] = 0.0;
                }
            }
        }

        return tfIdfMatrix;
    }
}
