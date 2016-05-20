CREATE TABLE documents (
    Id          serial,
    FileType    varchar(50),
    FileName    varchar(50),
    Length      smallint,
    Lemmas      varchar[]
);

/* INSERT INTO "documents" (FileType, Length, Lemmas) values (filetype, doc.size, doc); */
