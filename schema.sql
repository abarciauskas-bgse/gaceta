CREATE TABLE documents (
    Id          serial, # FIXME: should be a primary key
    # FIXME: Add SeqNum sequence in source document 
    FileType    varchar(50),
    FileName    varchar(50),
    Length      smallint,
    Lemmas      varchar[]
);

CREATE UNIQUE INDEX documents_id ON documents (Id);

CREATE TABLE alignments (
    Id serial PRIMARY KEY,
    FileName varchar(50),
    Doc1Id int REFERENCES documents (Id),
    Doc2Id int REFERENCES documents (Id),
    Score int
);

CREATE UNIQUE INDEX alignments_docIds_idx ON alignments (Doc1Id, Doc2Id);
