CREATE TABLE documents (
    Id          serial, # FIXME: should be a primary key
    # FIXME: Add SeqNum sequence in source document 
    FileType    varchar(50),
    FileName    varchar(50),
    Length      smallint,
    Lemmas      varchar[]
);


CREATE TABLE alignments (
    Id serial PRIMARY KEY,
    FileName varchar(50),
    doc1 int REFERENCES documents (Id),
    doc2 int REFERENCES documents (Id),
    score int
);
/* INSERT INTO "documents" (FileType, Length, Lemmas) values (filetype, doc.size, doc); */
