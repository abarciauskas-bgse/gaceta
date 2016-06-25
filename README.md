# Redundancy in Barcelona's Municipal Gazette

### Setup:

* Install freeling and its dependencies
* Install postgres and create tables detailed in `schema.sql`
* Download data from above sources to local directory (for me: `~/IdeaProjects/data/`)

### Data extraction process was as follows:

1. Copy data to temporary directory (it is deleted during the database writing process so that we know which files have already been processed if process needs to be killed or dies)

```bash
cp -r IdeaProjects/data/* IdeaProjects/temp_data
```

2. Update the code base to use the correct directory, default language, and stop words file for either the FOMC or Gaceta data.
3. Select a subinterval of time overwhich to conduct the analysis. For FOMC, a year is chosen, for Gaceta, a month-year pair was more reasonable.
3. Run `writeDocuments` which stores sentences as documents in the database (see `schema.sql` for more information)
4. Run `calcAndWriteAlignments` which calculates and stores Needleman-Wunsch sequence alignment scores
    * Calculating alignments for 1000 FOMC documents (e.g. 499500 alignments) took about 3 minutes, but this scales by a power so we would expect calculating alignments for 2000 documents to take 10 minutes (3**2).


----
## Alignment algorithms

### Needleman-Wunsch Algorithm

The Needleman-Wunsch Algorithm surfaces the best global alignment between 2 sequences of text. The Smith-Waterman alignment is a variation on Needleman-Wunsch which finds the best local alignment between 2 sequences of text. Both compute a scoring matrix in the same forward pass of the algorithm, with NW using negative values to indicate a gap penalty. SW does not include negative values, instead inserting 0's for gaps. In the backward pass to find the best global or local alignment, NW starts from the terminal cell and finds the highest scoring alignment for the whole strings, whereas SW starts from the cell with the highest score and returns the equivalent of the longest common subsequences between the 2 strings. 

NW has been implemented along with a `printAligment` function which returns an alignment pattern:

```
[|, de, |, |, |, |, |, 2000, |, per, |, |, |, |, |, |, |, |, |, |, |, |, |, |, |, |, --, --, |, |, |, |, per, ...]
[|, de, |, |, |, |, |, 2000, |, per, |, |, |, |, |, |, |, |, |, |, |, |, |, |, |, |, handbol, per, ...]
```

#### References

* [Needleman-Wunsch Algorithm (SlideShare)](http://www.slideshare.net/avrilcoghlan/the-needleman-wunsch-algorithm)
* [Needleman-Wunsch Algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm)
* [Smith-Waterman Algorithm (SlideShare)](http://www.slideshare.net/avrilcoghlan/the-smith-waterman-algorithm)
* [Smith-Waterman Algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Smith–Waterman_algorithm)
* [What is the difference between local and global sequence alignments?](http://biology.stackexchange.com/questions/11263/what-is-the-difference-between-local-and-global-sequence-alignments)

#### Complexity

Complexity of one comparision is `O(m x n)`. There are `N` of these comparisons where `N` is the number of documents at a given level. 

For SPLIT_NORM, omitting the greatest 1% of document length we have (Note: these numbers are for a subset of 970 out of a total 13,116 documents):

* Average length of document in SPLIT_NORM: 25
* Max length of document in SPLIT_NORM: 141
* Median length of document in SPLIT_NORM: 21

**Once there is an estimate for the time it takes to process two documents of average length, we can estimate running time on a given subset of N documents as `N*(N+1)/2*avg_running_time`**

The first document is compared with N-1 documents, the second document is compared with N-2 documents, etc.

----
## Summary Statistics of Corpora

* 560 PDF2TXT files, average file size 425927 bytes (426 KB)
* 13116 SPLIT_NORM files, average file size 13177 bytes (13 KB) (these numbers are near equal to values for SPLIT)

Unix script for average file size in directory:

```bash
ls -l PDF2TXT | awk '{sum += $5; n++;} END {print sum/n;}'
```

### Time to pre-process

* To read in 970 SPLIT_NORM documents into the `documents` database took 6 hours (162 documents / hour) so reading all 13116 files will take 80 hours

```psql
select count(distinct(FileName)) from documents;
```

