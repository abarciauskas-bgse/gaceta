# Measuring Redundancy in Text Corpora: An Overview of Current Methods

**This review and its subsequent efforts are a part of an on-going project to analyse the text of [Barcelona's Municipal Gazette](https://w33.bcn.cat/GasetaMunicipal/Inici).**

### Motivation:

E-government initiatives offer unprecedented transparency into government proceedings. But often their size and use of "administrative jargon" creates a barrier to understanding and parsing by the general public. Exploiting redundancy in these texts may facilitate the initiative to provide insight via summarization and visualition of these texts. To start the task of analyzing redundancy, what follows is a review of existing methods.

This is the motivation for evaluating redundancy in the current effort. Additional motivations are included as inspirational:

> Sentence similarity is considered the basis of many natural language tasks such as information retrieval, question answering and text summarization. ([A Comprehensive Comparative Study of Word and Sentence Similarity Measures][Atoum])

> ..redundancy can be exploited to identify important and accurate information for applications such as summarization and question answering ([Sentence Fusion for Multidocument News Summarization][McKeown & Barzilay]).

> ...most applications based on Twitter share the goal of providing tweets that are both informative and diverse... to keep a high level of diversity, redundant tweets should be removed from the set of tweets displayed to the user ([Linguistic Redundancy in Twitter][Zanzotto et al]).

> ...from a computational linguistic point of view, the high redundancy in micro-blogs gives the unprecedented opportunity to study classical tasks ... on very large corpora characterized by an original and emerging linguistic style, pervaded with ungrammatical and colloquial expressions, abbreviations, and new linguistic forms  ([Linguistic Redundancy in Twitter][Zanzotto et al]).

> O'Shea et al. applied text similarity in Conversational Agents, which are computer programs that interact with humans through natural language dialogue ([Text Similarity using Google Tri-grams][tri-grams]).

### Assumptions and Notation

* The words text and document may be used interchangeably.

* In what follows, an assumption is made measures of similarity can be used to measure redundancy, where redundancy in considered as a more strict definition of similarity. For example, "my dog ate my homework" and "my assignment was eaten by a canine" are both similar and redundant, whereas the former is completely similar, in one sense, to "My homework is to write about what my dog ate" but not redundant.

* `t1` and `t2` signify two separate text (or document) entities and the objective is to determine if `t1` is redundant with `t2` (e.g. boolean).

* Measurements of similarity (`Sim(t1,t2)`) may be evaluated at the levels of n-grams, phrases, sentences, paragraphs and anuncios. Only a subset of these levels is considered below, without loss of generality.

* The document-term matrix is a matrix of frequency counts of a term `i` in a document `d`. The matrix is then V x D dimension, where `D` is the total number of documents in the corpora and `V` is total number of unique terms.

### Methods to Measure Similarity and Redundancy

In the existing literature, measurements of similarity have been separated into **corpus-**, **knowledge-**, and **hybrid-based** methods. Hybrid methods are excluded from the current review.

The practical difference between corpus and knowledge-based methods is the corpus based depends on word frequences from a specific corpus. A restriction on corpus-based methods is they are quite domain-dependent and often do not generalize outside of a given corpus. This could pose a potential problem for the current efforts if required to measure redundancy across e-government initiatives, but this is not a current requirement, so this limitation is acceptable.

All methods listed below are included given their pertinence to the current problem, with the exception of methods listed in **Of Interest**.

#### Corpus-Based Word Similarity

The bag-of-words (BOW) method is often used as a baseline measurement of similarity between documents. Given a document-term matrix, taking the dot product or cosine of the dot-product between two columns (e.g. documents) gives a BOW-based similarity score of those two documents. The same method can be followed for the tf-idf version of this matrix.

[Latent Semantic Analysis][Latent Semantic Analysis] (LSA) measures the similarity between words using a word-count per document (e.g. words x documents, or transpose of the document-term matrix) matrix and computing the cosine of the dot product between 2 rows. This within-corpus word similarity measure will enrich measurements of similarity when comparing documents in methods for computing sentence-based similarities in what follows.

#### Knowlege-Based Word Similarity

WordNet bag-of-words (WBOW) is a "knowledge-based" version of Latent Semantic Analysis and is frequently used to enrich measurements of similarity in texts. WordNets are human-generated lexicons and thus do not require the pre-computation or corpus-dependency of LSA. WordNets are popular but may be limited in depth. It will be interesting to see what is available for Catalan.

#### Knowledge-Based Document Similarity

Knowledge-based document similarity measures listed in [Atoum][Atoum] use a knowledge-based measurement of word similarity within a document and some quantification for document structure. For example, measurments composed of [WBOW plus part-of-speech (POS) tree kernels][Tian et. al] or [POS tags][Lee]. Others are listed in [Atoum Section 2.2.2][Atoum]. Theses methods demonstrated poor results or were not evaluated in [Atoum][Atoum], and some of the more attractive versions are not available for review. For these reasons, focus will be on corpus-based methods (and possibly hybrid-based methods later on).

#### Corpus-Based Document Similarity

Corpus-based measures of document similarity rely on string similarity, string edit distance and word orders. Other common methods are the [edit-distance][https://en.wikipedia.org/wiki/Edit_distance] and [Smith-Waterman Alignment][Waterman Algorithm]. These methods will be used as baselines for more advanced methods, but also may provide valuable insights.

In [Atoum][Atoum], the highest-performing method was the [Google Tri-Gram][tri-grams] approach. This approach calculates a word-similarity metric using trigrams and then uses it in a subsequent text similarity metric. In essence, this metric evaluates the similarity of word `w_a` and `w_b` by measuring the frequency of tri-gram instances containing `w_a` and `w_b` in positions 1 and 3 of the trigram.

Tree-based measurments leverage a tree data-structure representation of a document (i.e. sentence). In [Linguistic Redundancy in Twitter][Zanzotto et al] the most successful formulation was a combination metric using WBOW and the Syntatic First-Order Rule Content Model (FOR). The FOR feature space introduced by [Zanzotto and Moschitti][Zanzotto 2006] constructs features as a pair of syntatic tree fragments augmented with variables which are evaluated for similarity.

[Simfinder][McKeown & Barzilay] also uses sentence syntax trees to compte sentence similarity, without expectation on their complate alignment.

## In conclusion: Overview of strategy for analysing redundancy in Barcelona's Municipal Gazette

#### Evaluation of methods

To evaluate results, the results of methods enumerated below will be compared with human-generated versions of the same metrics. For example, have human "experts" (i.e. native speakers) evaluate how many redundant sentences (or phrases or paragraphs) exist in one larger document and for each instance of redundant sentence, how many times is it repeated. Use the mean to evaluate the correlation with the results of the following algorithms.

#### Order of work

1. Extract sentences as documents (later on replace sentences with ngrams, phrases, parapgraphs, or anuncios)
2. Construct BOW and tf-idf document-term matrices
3. Evaluate similarity between documents using BOW and tf-idf (e.g. cosine of pair-wise columns)
4. Compute the LSA word-similariy and repeat 3 weighting counts and tf-idf scores with their LSA similarity score.
5. If WordNet is accessible for Catalan, repeat 4 using WordNet similarity.
6. Compute edit-distance
7. Compute Smith-Waterman Alignment
8. Advanced methods will be attempted in the following order: [Google tri-gram][tri-grams], [Simfinder][McKeown & Barzilay], and [WBOW + FOR][Zanzotto et al]

#### Of Interest

* In [Atoum][Atoum], [Semantic Text Similarity Using Corpus-Based Word Similarity and String Similarity][Islam] is used in the evaluation and the paper is available so this method may be implemented. It has not yet been reviewed which is why it is not featured in the **Corpus-Based Document Similarity** section of this document.
* [RedLDA][RedLDA] is redundancy aware topic modelling builds on LDA topic modelling to address the assumption that words are sampled from a topic distribution, where in fact in the case there is redundancy they are copied from another document. It was used to identify copy-paste instances in patient heath records.
    * Uses [Smith-Waterman Alignment][Waterman Algorithm] to characterize redundancy.
    * The online fingerprinting method is used to identify which string are unique within a record.
    * [Latent Dirichlet Allocation][Latent Dirichlet Allocation] is a generative probabilistic model of text corpora.
* [Identifying feature redundancy using a Markov blanket][Yu & Liu] (1208), identifies highly correlated features using symmetrical uncertainty (1212), identifies predomininat features (e.g. does not have any approximate Markov blanket in the current set) and then removing all features for which it forms an approximate Markov blanket.

[RedLDA]: http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0087555
[Yu & Liu]: http://www.jmlr.org/papers/volume5/yu04a/yu04a.pdf
[Latent Dirichlet Allocation]: http://dl.acm.org/citation.cfm?id=944937
[McKeown & Barzilay]: http://www.mitpressjournals.org/doi/pdf/10.1162/089120105774321091
[Waterman Algorithm]: https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm
[Municipal Gazette]: https://w33.bcn.cat/GasetaMunicipal/Inici
[Catalan WordNet]: http://www.cs.upc.edu/~nlp/projectes/cwn.html
[Zanzotto et al]: http://www.aclweb.org/anthology/D11-1061.pdf
[Latent Semantic Analysis]: https://en.wikipedia.org/wiki/Latent_semantic_analysis
[Tian et. al]: http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=5713120&searchWithin%3Dkernel%26filter%3DAND%28p_IS_Number%3A5713046%29
[Lee]: http://www.sciencedirect.com/science/article/pii/S0957417410011875?np=y
[Atoum]: https://www.researchgate.net/publication/294873785_A_Comprehensive_Comparative_Study_of_Word_and_Sentence_Similarity_Measures
[Islam]: https://www.site.uottawa.ca/~diana/publications/tkdd.pdf
[tri-grams]: https://web.cs.dal.ca/~eem/cvWeb/pubs/2012-Aminul-CAI.pdf
[Zanzotto 2006]: http://www.aclweb.org/anthology/P06-1051
