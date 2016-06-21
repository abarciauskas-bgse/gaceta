package com.gaceta;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.sql.*;
import java.util.*;
import edu.upc.freeling.*;
import static java.nio.file.StandardCopyOption.*;

public class Main {
    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String LANG = "ca";
    private static final String FILETYPE = "SPLIT_NORM";
    private static final java.io.File FOLDER = new java.io.File("/Users/aimeebarciauskas/GACETA/" + FILETYPE);
    private static final int minDocLength = 4;

    public static Connection dbConnect() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/gaceta", "abarciauskas", "");
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
    public static void calcAndWriteAlignments() {
        Alignment alignment = new Alignment();
        Connection dbConnection = dbConnect();
        Statement stmt = null;
        ArrayList<String> filenames = new ArrayList<>();
        String fileQueryString = "SELECT distinct(FileName) from processed_documents;";
        try {
            stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(fileQueryString);
            // Fetch each row from the result set
            while (rs.next()) {
                String name = rs.getString("FileName");
                filenames.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println("Total number of distinct documents: " + filenames.size());


        for (int fidx = 0; fidx < filenames.size(); fidx ++) {
            String filename = filenames.get(fidx);
            System.out.println("Calculating alignments for " + filename + ", file idx: " + fidx);
            String docsQueryString = "SELECT Id, TaggedLemmas from processed_documents where FileName = '" + filename + "';";
            try {
                ResultSet rs = stmt.executeQuery(docsQueryString);
                ArrayList<HashMap> documentsArr = new ArrayList<>();
                while(rs.next()) {
                    int Id = rs.getInt("Id");
                    String[] docLemmas = (String[]) rs.getArray("TaggedLemmas").getArray();
                    HashMap docMap = new HashMap();
                    docMap.put("Id", Id);
                    docMap.put("TaggedLemmas", docLemmas);
                    documentsArr.add(docMap);
                }

                for (int doc1idx = 0; doc1idx < documentsArr.size(); doc1idx++) {
                    HashMap doc1 = documentsArr.get(doc1idx);
                    int doc1id = (int) doc1.get("Id");
                    String[] doc1lemmas = (String[]) doc1.get("TaggedLemmas");

                    for (int doc2idx = 0; doc2idx < documentsArr.size(); doc2idx++) {
                        // so we don't double count
                        if (doc2idx > doc1idx) {
                            HashMap doc2 = documentsArr.get(doc2idx);
                            int doc2id = (int) doc2.get("Id");
                            String[] doc2lemmas = (String[]) doc2.get("TaggedLemmas");

                            int score = (int) alignment.needlemanWunsch(doc1lemmas, doc2lemmas, false).get("score");
                            String updateSqlString = "INSERT INTO alignments (FileName, Doc1Id, Doc2Id, Score) values ('"
                                    + filename + "','" + doc1id + "','" + doc2id + "','" + score + "');";
                            stmt.executeUpdate(updateSqlString);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error: " + e.getMessage());
            }

        }
    }

    public static void writeDocuments() {
        try {
            Connection dbConnection = dbConnect();
            Statement stmt = null;
            String sql = null;

            for (final File fileEntry : FOLDER.listFiles()) {
                Corpus corpus;
                corpus = new Corpus(fileEntry.getAbsolutePath());
                for (int i = 0; i < corpus.documents.size(); i++) {
                    // store every document
                    ArrayList<String> doc = corpus.documents.get(i);
                    ArrayList<String> rawDoc = corpus.rawDocuments.get(i);
                    String ogDoc = corpus.ogDocuments.get(i);
                    if (doc.size() > 0) {
                        stmt = dbConnection.createStatement();
                        if (doc.size() >= minDocLength) {
                            String docWordsString = "";
                            for (String s : doc) {
                                docWordsString += "\"" + s.replace("'", "''") + "\",";
                            }
                            docWordsString = docWordsString.substring(0, docWordsString.length() - 1);
                            docWordsString = "{" + docWordsString + "}";

                            String rawDocWordsString = "";
                            for (String sraw : rawDoc) {
                                rawDocWordsString += "\"" + sraw.replace("'", "''") + "\",";
                            }
                            rawDocWordsString = rawDocWordsString.substring(0, rawDocWordsString.length() - 1);
                            rawDocWordsString = "{" + rawDocWordsString + "}";

                            //System.out.println(ogDoc);
                            //System.out.println("");
                            String replacedString = ogDoc.replace("'", "");

                            sql = "INSERT INTO processed_documents (FileType, FileName, Length, RawLemmas, TaggedLemmas, Original) values ('"
                                    + FILETYPE + "','"
                                    + fileEntry.getName() + "','"
                                    + doc.size() + "','"
                                    + rawDocWordsString + "','"
                                    + docWordsString + "','"
                                    + replacedString
                                    + "');";
                            stmt.executeUpdate(sql);
                        }
                    }
                }
                System.out.println("Done inserting file: " + fileEntry.getName());
                File afile = new File(fileEntry.getAbsolutePath());
                afile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        System.load("/Users/aimeebarciauskas/Projects/freeling_src/APIs/java/libfreeling_javaAPI.dylib");
//
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

        try {
            writeDocuments();
            //calcAndWriteAlignments();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }
}

