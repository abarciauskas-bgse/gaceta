package com.gaceta;

import edu.upc.freeling.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by abarciauskas on 5/11/16.
 */
public class Corpus {
    static ArrayList<ArrayList<String>> documents;
    static HashMap termDictionary;
    static int[][] documentTermMatrix;
    static Double[][] tfIdfMatrix;
    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String LANG = "ca";

    public Corpus (String filename)
    {
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

        Maco mf = new Maco( op );
        mf.setActiveOptions(false, true, true, true,  // select which among created
                true, true, false, true,  // submodules are to be used.
                true, true, true, true);  // default: all created submodules

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

        documents = readDocuments(br, mf);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList readDocuments(BufferedReader br, Maco mf) {
        LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident.dat");
        ArrayList<ArrayList<String>> documents = new ArrayList<>();
        Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
        Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
        SWIGTYPE_p_splitter_status sid = sp.openSession();

        // instantiate list of documents
        //Read File Line By Line
        try {
            String line;
            while ((line = br.readLine()) != null) {

                // Identify language of the text.
                // Note that this will identify the language, but will NOT adapt
                // the analyzers to the detected language.  All the processing
                // in the loop below is done by modules for LANG (set to "es" at
                // the beggining of this class) created above.
                //String lg = lgid.identifyLanguage(line);
                //System.out.println( "-------- LANG_IDENT results -----------" );
                //System.out.println("Language detected (from first line in text): " + lg);

                // Extract the tokens from the line of text.
                ListWord l = tk.tokenize(line);

                // Split the tokens into distinct sentences.
                ListSentence ls = sp.split(sid, l, false);
                mf.analyze(ls);
                ListSentenceIterator sIt = new ListSentenceIterator(ls);
;
                while (sIt.hasNext()) {
                    Sentence s = sIt.next();
                    ArrayList<String> docWords = new ArrayList<>();
                    ListWordIterator wIt = new ListWordIterator(s);
                    while (wIt.hasNext()) {
                        Word w = wIt.next();
                        String ts = w.getTag();
                        // remove punctuation
                        // FIXME: feels hacky, maybe use snowball?
                        if (!ts.contains("F")) {
                            docWords.add(w.getLemma());
                        }
                    }
                    documents.add(docWords);
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
            ArrayList<String> document = documents.get(i);
            for (int j = 0; j < document.size(); j++) {
                String word = document.get(j);
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