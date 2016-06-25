CREATE TABLE corpii (
    Id         serial PRIMARY KEY,
    Year       varchar(20),
    TermVector varchar[]
);

CREATE UNIQUE INDEX corpii_id ON corpii (Id);

CREATE TABLE processed_documents (
    Id                    serial PRIMARY KEY,
    CorpiiId              int REFERENCES corpii (Id),
    FileType              varchar(50),
    Year                  varchar(20),
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
    Score int,
    Year varchar(20),
    CosineSimilarity decimal
);

CREATE UNIQUE INDEX alignments_id ON alignments (Id);
CREATE UNIQUE INDEX alignments_docIds_idx ON alignments (Doc1Id, Doc2Id);

# DROP TABLE alignments;
# DROP TABLE processed_documents;
