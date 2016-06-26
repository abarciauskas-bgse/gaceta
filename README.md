# Redundancy in Barcelona's Municipal Gazette

## Running the code

### Before you start:

* Install postgres and create databases `gaceta` and `fomc` each having tables using `schema.sql`

### 1: Manual data extraction (Option 1)

This part is optional, as it requires a working java environment. To skip this step move to **1: Import data from sql dump (Option 2)**

1. Install freeling and its dependencies
2. Download data from [fomc](http://stanford.edu/~rezab/useful/fomc_minutes.html) and store in a local directory (e.g.: `~/IdeaProjects/data/`). Data for the gaceta database is only available via BSC ftp server.
2. Update the code base with the following respective of FOMC and Gaceta data sources:
   1. to use the correct local directory (in `Main.java`),
   2. `use_porter = true` if english (in `Corpus.java`),
   3. and stopwords file for english or catalan (`empty.ca` and `empty.en` in `Corpus.java`).
3. Select a subinterval (`YEAR` in `Main.java`) of time overwhich to conduct the analysis. For FOMC, a year is chosen, for Gaceta, a month-year pair was more reasonable.
3. Run `writeDocuments` which stores sentences as documents in the database (see `schema.sql` for more information)
4. Run `calcAndWriteAlignments` which calculates and stores Needleman-Wunsch sequence alignment scores
    * Calculating alignments for 1000 FOMC documents (e.g. 499500 alignments) took about 3 minutes, but this scales by a power so we would expect calculating alignments for 2000 documents to take 10 minutes (3**2).

### 1: Import data from sql dump (Option 2)

Download sql dump file(s):

**Google Drive:**

* [gaceta_db.sqldump](https://drive.google.com/file/d/0B39HWOgUiKJraHFaenAwTVZ5RW8/view?usp=sharing)
* [fomc_db.sqldump](https://drive.google.com/file/d/0B39HWOgUiKJrbzRPTkxBUGFXYUU/view?usp=sharing)

**Amazon S3**

* [gaceta_db.sqldump](https://s3-eu-west-1.amazonaws.com/abarciauskas-bgse/text_mining_data/gaceta_db.sqldump)
* [fomc_db.sqldump](https://s3-eu-west-1.amazonaws.com/abarciauskas-bgse/text_mining_data/fomc_db.sqldump)


### 2: Analysis

Once the database has been imported, analysis for each separate corpora can be found in `python_scripts`.

Ensure to update the username when connecting to your database.


----
## Summary Statistics of Corpora

* 560 PDF2TXT files, average file size 425927 bytes (426 KB)
* 13116 SPLIT_NORM files, average file size 13177 bytes (13 KB) (these numbers are near equal to values for SPLIT)
* Average number of sentences in SPLIT_NORM: 25
* Max: 141
* Median: 21

Unix script for average file size in directory:

```bash
ls -l PDF2TXT | awk '{sum += $5; n++;} END {print sum/n;}'
```

### Time to pre-process

* To read in 970 SPLIT_NORM documents into the `documents` database took 6 hours (162 documents / hour) so reading all 13116 files will take 80 hours

