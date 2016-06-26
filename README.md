# Redundancy in Barcelona's Municipal Gazette

### Setup:

* Install freeling and its dependencies
* Install postgres and create tables detailed in `schema.sql`
* Download data from above sources to local directory (for me: `~/IdeaProjects/data/`)

### Data extraction process was as follows:

1. Copy data to directory (it is deleted during the database writing process so that we know which files have already been processed if process needs to be killed or dies)

```bash
cp -r IdeaProjects/data/* IdeaProjects/temp_data
```

2. Update the code base to use the local directory (in `Main.java`), `use_porter = true` if english (in `Corpus.java`), and stopwords file for either the FOMC or Gaceta data.
3. Select a subinterval (`YEAR` in `Main.java`) of time overwhich to conduct the analysis. For FOMC, a year is chosen, for Gaceta, a month-year pair was more reasonable.
3. Run `writeDocuments` which stores sentences as documents in the database (see `schema.sql` for more information)
4. Run `calcAndWriteAlignments` which calculates and stores Needleman-Wunsch sequence alignment scores
    * Calculating alignments for 1000 FOMC documents (e.g. 499500 alignments) took about 3 minutes, but this scales by a power so we would expect calculating alignments for 2000 documents to take 10 minutes (3**2).


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

