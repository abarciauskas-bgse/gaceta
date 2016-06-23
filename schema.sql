CREATE TABLE processed_documents (
    Id                    serial PRIMARY KEY,
    FileType              varchar(50),
    Year                  smallint,
    Length                smallint,
    RawLemmas             varchar[],
    TaggedLemmas          varchar[],
    TfIdfVector           decimal[],
    Original              text
);

CREATE UNIQUE INDEX processed_documents_id ON processed_documents (Id);

CREATE TABLE alignments (
    Id serial PRIMARY KEY,
    Doc1Id int REFERENCES processed_documents (Id),
    Doc2Id int REFERENCES processed_documents (Id),
    Score int
);

CREATE UNIQUE INDEX alignments_docIds_idx ON alignments (Doc1Id, Doc2Id);

# DROP TABLE alignments;
# DROP TABLE processed_documents;
