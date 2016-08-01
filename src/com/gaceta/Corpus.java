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
    static ArrayList<ArrayList<String>> rawDocuments;
    static ArrayList<String> ogDocuments;
    static HashMap termDictionary;
    static int[][] documentTermMatrix;
    static Double[][] tfIdfMatrix;
    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String LANG = "ca";
    private static final boolean use_porter = true;

    public Corpus (File[] filenames)
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

        ArrayList<BufferedReader> brs = new ArrayList<>();

        for (File file : filenames) {
            String filename = file.getAbsolutePath();
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
            brs.add(br);
        }


        this.readDocuments(brs, mf);
    }

    public static ArrayList genStopwords() {
        ArrayList<String> stopwords = new ArrayList<>();
        FileInputStream fstream = null;
        try {
            //http://www.cs.upc.edu/~padro/index.php?page=nlp
            // FIXME: dependent on lang once that is fixed
            fstream = new FileInputStream("/Users/aimeebarciauskas/IdeaProjects/gaceta/empty.en");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        try {
            String line;
            while ((line = br.readLine()) != null) {
                stopwords.add(line);
            }
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getStackTrace());
        }

        return stopwords;
    }

    // QUANT, DATE, MISC, PER, LOC, ORG
    public static String replaceNamedEntities(TokenF tf) {
        PorterStemmer stemmer = new PorterStemmer();
        String replaceNamedEntities;
        // FIXME: not intended use
        if (tf.isQuantity()) {
            replaceNamedEntities = "QUANT";
        } else if (tf.isDate()) {
            replaceNamedEntities = "DATE";
        } else if (tf.isMiscellanea()) {
            replaceNamedEntities = "MISC";
        } else if (tf.isPerson()) {
            replaceNamedEntities = "PER";
        } else if (tf.isLocation()) {
            replaceNamedEntities = "LOC";
        } else if (tf.isOrganization()) {
            replaceNamedEntities = "ORG";
        } else if (tf.isProperNoun()) {
            replaceNamedEntities = "NP";
        } else {
            if (use_porter) {
                replaceNamedEntities = stemmer.stem(tf.getLemma());
            } else {
                replaceNamedEntities = tf.getLemma();
            }
        }

        return replaceNamedEntities;
    }

    public void readDocuments(ArrayList<BufferedReader> brs, Maco mf) {
        LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident.dat");
        ArrayList<ArrayList<String>> documents = new ArrayList<>();
        ArrayList<ArrayList<String>> rawDocuments = new ArrayList<>();
        ArrayList<String> ogDocuments = new ArrayList<>();
        Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
        Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
        SWIGTYPE_p_splitter_status sid = sp.openSession();

        ArrayList<String> stopwords = genStopwords();
        int counter = 0;
        for (BufferedReader br : brs) {
            counter += 1;
            System.out.println("Processing file: " + counter + " of " + brs.size());
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    // Extract the tokens from the line of text.
                    ListWord l = tk.tokenize(line);

                    // Split the tokens into distinct sentences.
                    ListSentence ls = sp.split(sid, l, false);
                    mf.analyze(ls);
                    ListSentenceIterator sIt = new ListSentenceIterator(ls);
                    int sentenceSeq = 0;

                    while (sIt.hasNext()) {
                        Sentence s = sIt.next();
                        SentenceF sf = new SentenceF(s, sentenceSeq);
                        sentenceSeq++;
                        ArrayList<String> docWords = new ArrayList<>();
                        ArrayList<String> rawDocWords = new ArrayList<>();
                        String ogDoc = "";
                        ListWordIterator wIt = new ListWordIterator(s);
                        int wordseq = 0;
                        while (wIt.hasNext()) {
                            Word w = wIt.next();
                            TokenF tokenf = new TokenF(sf, w, wordseq);
                            String tag = tokenf.getPOS();
                            String lemma = w.getLemma();
                            wordseq++;
                            // remove punctuation and stopwords
                            // FIXME: feels hacky, maybe use snowball?
                            if (!(tag.matches("F(.*)") || stopwords.contains(lemma))) {
                                String lemmaOrEntity = replaceNamedEntities(tokenf);
                                docWords.add(lemmaOrEntity);
                                rawDocWords.add(lemma);
                            }
                            if (tag.matches("F(.*)")) {
                                ogDoc += tokenf.getStr();
                            } else {
                                if (wordseq > 1) {
                                    ogDoc += " " + tokenf.getStr();
                                } else {
                                    ogDoc += tokenf.getStr();
                                };
                            }
                        }
                        documents.add(docWords);
                        rawDocuments.add(rawDocWords);
                        ogDocuments.add(ogDoc);
                    }
                }

            } catch (Exception e) {
                System.err.println("Corpus error: " + e.getMessage() + ", stack trace: ");
                e.printStackTrace();
            }
            continue;
        }
        // instantiate list of documents
        //Read File Line By Line

        this.rawDocuments = rawDocuments;
        this.documents = documents;
        this.ogDocuments = ogDocuments;
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
