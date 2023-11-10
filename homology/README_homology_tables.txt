Homology Table README
11/7/2007
ftp://ftp.flybase.net/genomes/12_species_analysis/clark_eisen/homology

===================


frb.GLEANR.TE*.tsv
------------------

TSV file containing the clusters of homologous gene models from the 12
species, for the GLEANR set including and excluding models with hits
to TEs.


Column1: cluster_id

Column2: is the 'classification', showing the number of homologous
models ( 1=>ortholog, n=>potential paralogs) from each species group
in the homolog cluster.

Columns3-14: are the homologous groups from each species, in
alphabetical order.

Species are in ___alphabetical___ order, each species gets a 0,1, or n.

If a cluster has no representation from a species, the corresponding
column contains '.'.


GeneWise.revised.homology.tsv
-----------------------------

For each protein-coding gene in D. melanogaster as of FlyBase Release 4.3,
we attempted to verify absences in the other 11 sequenced genomes by using
a GeneWise pipeline to augment the fuzzy reciprocal BLAST (FRB) homology calls.
For further details see Drosophila 12 Genomes Consortium, Evolution of genes 
and genomes on the Drosophila phylogeny.Nature doi:10.1038/nature06341

The revised orthology calls are coded as follows:
1: present (as either a single-copy ortholog or a multi-copy paralog; 1 does not
indicate that only a single ortholog is present, it simply means that there is a
homolog of the indicated D. melanogaster gene in that species)

0: absent (no homolog detected via either the GeneWise pipeline or the FRB homology
calls)

A: ambiguous (no homolog detected, but assembly gaps or other sequence quality issues
exist that could explain absence)

X: not run (pipeline not run because potential homologous region was too large)

The twelve-digit orthology code lists the species in the following order:
D. melanogaster
D. sechellia
D. simulans
D. yakuba
D. erecta
D. ananassae
D. pseudoobscura
D. persimilis
D. willistoni
D. mojavensis
D. virilis
D. grimshawi
