This supplemental material corresponds to the DCJ Median Genetic Algorithm Medianizer 2000 created by Ethan Smith, Aaron Dias Barreto, and Andrew Farkas.

The python scripts contained in this supplemental material can be used to generate the Drosophila orthology dataset that was used as the real-world data example when testing the algorithm.

The following python packages are required to run these scripts:
1. Pandas - https://pandas.pydata.org/

Tested on Python v3.11.3


Scripts should be run in order of the preceeding number:

1. orthodb_homology_info/1_get_homology_info.py - Downloads the corresponding ortholog data from OrthoDB.org

2. orthodb_homology_info/2_format_gene_names.py - Searches gene aliases against the NCBI Invertebrate Gene Information database retrieved from https://www.ncbi.nlm.nih.gov/public/ (NCBI FTP Path: gene > DATA > GENE_INFO > Invertebrates > All_Invertebrates.gene_info.gz)

3. 3_remove_invalid_genes.py - removes ortholog groups which contain genes that do not match to any annotated genes in their respective species annotation

4. 4_sort_orthologs_by_chromosome - sorts the ortholog groups by chromosome names in each of the 9 Drosophila species, also adds Chromosome, Strand, and Start position information for each gene

5. 5_create_permutations.jl - julia script to generate the signed gene order permutations from the ortholog data for use with the Genetic Algorithm



The final CSV file used with 5_create_permutations.jl will be named ortholog_data_final_sorted.csv.
