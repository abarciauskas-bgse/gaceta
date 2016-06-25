package com.gaceta;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import edu.upc.freeling.*;
import org.omg.PortableInterceptor.ServerRequestInfo;

import static java.nio.file.StandardCopyOption.*;

public class Main {
    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String DATABASE = "fomc";
    private static final String FILETYPE = "gold_data/minutes/txt";
    //private static final String DATABASE = "gaceta";
    //private static final String FILETYPE = "SPLIT_NORM";
    private static final java.io.File FOLDER = new java.io.File("/Users/aimeebarciauskas/IdeaProjects/temp_data/" + FILETYPE);
    private static final int minDocLength = 4;
    private static final String YEAR = "2006";

    public static Connection dbConnect() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/" + DATABASE, "abarciauskas", "");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c;
    }

    // finds all documents by filename
    // calculates alignments and stores them in db
    public static void calcAndWriteAlignmentsRandom() {
        Alignment alignment = new Alignment();
        Connection dbConnection = dbConnect();
        Statement stmt = null;

        String docsQueryString = "SELECT Id,TaggedLemmas,TfIdfVector from processed_documents WHERE Year = '" + YEAR + "';";
        System.out.print(docsQueryString);
        try {
            stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(docsQueryString);
            ArrayList<HashMap> documentsArr = new ArrayList<>();
            while(rs.next()) {
                int Id = rs.getInt("Id");
                String[] docLemmas = (String[]) rs.getArray("TaggedLemmas").getArray();
                BigDecimal[] docTfIdf = (BigDecimal[]) rs.getArray("TfIdfVector").getArray();
                HashMap docMap = new HashMap();
                docMap.put("Id", Id);
                docMap.put("TaggedLemmas", docLemmas);
                docMap.put("TfIdfVector", docTfIdf);
                documentsArr.add(docMap);
            }

            for (int doc1idx = 0; doc1idx < documentsArr.size(); doc1idx++) {
                System.out.println("Comparing doc: " + doc1idx + " of " + documentsArr.size());
                HashMap doc1 = documentsArr.get(doc1idx);
                int doc1id = (int) doc1.get("Id");
                String[] doc1lemmas = (String[]) doc1.get("TaggedLemmas");
                BigDecimal[] doc1tfIdfVector = (BigDecimal[]) doc1.get("TfIdfVector");
                for (int doc2idx = 0; doc2idx < documentsArr.size(); doc2idx++) {
                    // Don't double count
                    if (doc2idx > doc1idx) {
                        HashMap doc2 = documentsArr.get(doc2idx);
                        int doc2id = (int) doc2.get("Id");
                        String[] doc2lemmas = (String[]) doc2.get("TaggedLemmas");
                        BigDecimal[] doc2tfIdfVector = (BigDecimal[]) doc2.get("TfIdfVector");

                        int score = (int) alignment.needlemanWunsch(doc1lemmas, doc2lemmas, false).get("score");
                        Double cosineSimilarity = Utils.cosineSimilarity(doc1tfIdfVector, doc2tfIdfVector);
                        String updateSqlString = "INSERT INTO alignments (Doc1Id, Doc2Id, Year, NwScore, CosineSimilarity) values ('"
                                + doc1id + "','" + doc2id + "','" + YEAR + "','" + score + "','" + cosineSimilarity + "');";

                        try {
                            stmt.executeUpdate(updateSqlString);
                        } catch (org.postgresql.util.PSQLException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());

        }

    }

    public static void writeDocuments() {
        try {
            Connection dbConnection = dbConnect();
            Statement stmt = null;
            String sql = null;

            // get max id from database
            sql = "SELECT max(Id) FROM corpii;";
            stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            int currCorpiiId = result.findColumn("max");
            currCorpiiId = 5; //FIXME: SHOULD BE currCorpiiId + 1;
            System.out.println("Corpii id: " + currCorpiiId);

            Corpus corpus;
            File[] files = FOLDER.listFiles((d, name) -> name.matches(YEAR + "(.*)"));
            corpus = new Corpus(files);
            corpus.createTermDictionary();
            corpus.createDocumentTermMatrix();
            corpus.createTfIdfMatrix();
            List<String> terms = new ArrayList<String>(corpus.termDictionary.keySet());
            String termVectorString = Utils.databaseArrayString(terms);
            stmt = dbConnection.createStatement();
            sql = "INSERT INTO corpii (Id, Year, TermVector) values ('"
                    + currCorpiiId + "','"
                    + YEAR + "','"
                    + termVectorString
                    + "');";
            stmt.executeUpdate(sql);
            try {
                for (int i = 0; i < corpus.documents.size(); i++) {
                    // store every document
                    ArrayList<String> doc = corpus.documents.get(i);
                    float occurrences = Collections.frequency(doc, "NP");
                    ArrayList<String> rawDoc = corpus.rawDocuments.get(i);
                    String ogDoc = corpus.ogDocuments.get(i);
                    java.lang.Double[] tfidfVector = corpus.tfIdfMatrix[i];

                    if (doc.size() > 0 && occurrences/doc.size() < 0.5) {
                        stmt = dbConnection.createStatement();
                        if (doc.size() >= minDocLength) {
                            String docWordsString = Utils.databaseArrayString(doc);
                            String rawDocWordsString = Utils.databaseArrayString(rawDoc);
                            String tfidfArr = Utils.databaseArrayDoubleString(tfidfVector);

                            String replacedString = ogDoc.replace("'", "");
                            sql = "INSERT INTO processed_documents (CorpiiId, FileType, Year, Length, RawLemmas, TaggedLemmas, TfIdfVector, Original) values ('"
                                    + currCorpiiId + "','"
                                    + FILETYPE + "','"
                                    + YEAR + "','"
                                    + doc.size() + "','"
                                    + rawDocWordsString + "','"
                                    + docWordsString + "','"
                                    + tfidfArr + "','"
                                    + replacedString
                                    + "');";
                            stmt.executeUpdate(sql);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        System.load("/Users/aimeebarciauskas/Projects/freeling_src/APIs/java/libfreeling_javaAPI.dylib");

        try {
            writeDocuments();
            final long startTime = System.currentTimeMillis();
            calcAndWriteAlignmentsRandom();
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime) );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }
}

