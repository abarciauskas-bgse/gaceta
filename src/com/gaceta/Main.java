package com.gaceta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;

import java.util.*;

public class Main {

    public static Double[][] createTfIdfMatrix(int[][] documentTermMatrix, HashMap<String, Integer> termDictionary) {
        int N = documentTermMatrix.length;
        Double[][] tfIdfMatrix = new Double[N][termDictionary.size()];
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

    // createDocumentTermMatrix takes an array list of documents which contains an array of string lists
    public static int[][] createDocumentTermMatrix(HashMap<String, Integer> termDictionary, ArrayList<ArrayList<String>> documents) {
        int[][] documentTermMatrix = new int[documents.size()][termDictionary.size()];

        for (int i = 0; i < documents.size(); i++) {
            ArrayList document = documents.get(i);
            for (int j = 0; j < document.size(); j++) {
                Object word = document.get(j);
                int termIndex = termDictionary.get(word);
                int currentCount = documentTermMatrix[i][termIndex];
                documentTermMatrix[i][termIndex] = currentCount + 1;
            }

        }
        return documentTermMatrix;
    }

    // creates a dictionary of terms {term: index}
    public static HashMap createTermDictionary(ArrayList<ArrayList<String>> documents) {
        HashMap<String, Integer> termDictionary = new HashMap<>();

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
    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String LANG = "ca";

    // FIXME: This stuff may already be done by FreeLing
    public static void main(String[] args) {
        // FIXME: Getting java.lang.UnsatisfiedLinkError: edu.upc.freeling.freelingJNI.new_MacoOptions(Ljava/lang/String;)J error
//        System.load("/Users/aimeebarciauskas/Projects/FreeLing-4.0-beta2/APIs/java/libfreeling_javaAPI.so");
//
//        //Util.initLocale( "default" );
//        //Util.initLocale("default");
//
//        // Create options set for maco analyzer.
//        // Default values are Ok, except for data files.
//        MacoOptions op = new edu.upc.freeling.MacoOptions( LANG );
//
//        op.setDataFiles( "",
//                DATA + "common/punct.dat",
//                DATA + LANG + "/dicc.src",
//                DATA + LANG + "/afixos.dat",
//                "",
//                DATA + LANG + "/locucions.dat",
//                DATA + LANG + "/np.dat",
//                DATA + LANG + "/quantities.dat",
//                DATA + LANG + "/probabilitats.dat");

        try {
            String file;
            file = "/Users/aimeebarciauskas/IdeaProjects/gaceta/data/1-2000_8.txt";
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ArrayList<ArrayList<String>> documents;
            documents = readDocuments(br);
            HashMap termDictionary = createTermDictionary(documents);
            //Close the input stream
            //System.out.println(Arrays.toString(termDictionary.entrySet().toArray()));
            System.out.println("Min value in term dictionary: " + Collections.min(termDictionary.values()));
            System.out.println("Max value in term dictionary: " + Collections.max(termDictionary.values()));
            System.out.println("Length of term dictionary: " + termDictionary.size());
            System.out.println("Total size of collected documents:" + documents.size());
            //System.out.println(Arrays.toString(termDictionary.entrySet().toArray()));

            // SANITY CHECK METHODS
            int[][] termMatrix = createDocumentTermMatrix(termDictionary, documents);
            ArrayList terms = new ArrayList(termDictionary.keySet());
//            for (int j = 1; j < terms.size(); j++) {
//                Object term = terms.get(j);
//                int termindx = (int) termDictionary.get(term);
//                int termcount = termMatrix[0][termindx];
//                if (termcount > 0) {
//                    System.out.print(term + ": " + termcount + ", ");
//                }
//            }

            Double[][] tfIdfMatrix = createTfIdfMatrix(termMatrix, termDictionary);
            for (int j = 1; j < terms.size(); j++) {
                Object term = terms.get(j);
                int termindx = (int) termDictionary.get(term);
                Double tfidf = tfIdfMatrix[0][termindx];
                if (tfidf > 0) {
                    System.out.print(term + ": " + tfidf  + ", ");
                }
            }

            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

