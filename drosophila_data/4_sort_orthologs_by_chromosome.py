import pandas as pd
import gzip
import os
import glob

def parse_gff3(file_name):
    with gzip.open(file_name, 'rt') as file:
        gene_to_chromosome = {}
        gene_to_start_pos = {}
        gene_to_strand = {}
        for line in file:
            if not line.startswith('#'):
                parts = line.split('\t')
                if parts[2] == "gene":
                    chromosome, start_pos, strand = parts[0], parts[3], parts[6]
                    attributes = parts[8]
                    gene_info = [attr for attr in attributes.split(';') if attr.startswith('Name=') or attr.startswith('gene_synonym=')]
                    for info in gene_info:
                        key, value = info.split('=')
                        genes = value.split(',')
                        for gene in genes:
                            gene_to_chromosome[gene] = chromosome
                            gene_to_start_pos[gene] = start_pos
                            gene_to_strand[gene] = strand
    return gene_to_chromosome, gene_to_start_pos, gene_to_strand

def find_gff_path(species):
    pattern = os.path.join(species, "ncbi_dataset", "data", "GCF_*", "genomic.gff.gz")
    matching_paths = glob.glob(pattern)
    if matching_paths:
        return matching_paths[0]
    else:
        raise FileNotFoundError(f"No GFF file found for {species}!")

species_list = [
    "Dananassae", "Dsimulans", "Dyakuba", "Dsuzukii", "Dmiranda",
    "Dmauritiana", "Dsechellia", "Dsantomea", "Dmelanogaster"
]

gff3_files = {species: find_gff_path(species) for species in species_list}

# Create species-specific dictionaries
species_data = {}
for species, file in gff3_files.items():
    chrom_data, start_data, strand_data = parse_gff3(file)
    species_data[species] = {'chromosome': chrom_data, 'start': start_data, 'strand': strand_data}

csv_file = os.path.join("orthodb_homology_info", "filtered_ortholog_data_formatted.csv")

# Read the existing CSV file
df = pd.read_csv(csv_file)

# Rename the first column
df.rename(columns={df.columns[0]: 'Ortholog_Group_ID'}, inplace=True)

# Rename gene columns and add new columns for each species
for species in gff3_files.keys():
    species_col_name = (species[0] + "rosophila " + species[1:])
    new_species_col_name = species + "_Gene"
    df.rename(columns={species_col_name: new_species_col_name}, inplace=True)

    # Chromosome column
    chromosome_col = species + '_Chromosome'
    df.insert(df.columns.get_loc(new_species_col_name) + 1, 
              chromosome_col, 
              df[new_species_col_name].map(species_data[species]['chromosome']))

    # Start position column
    start_col = species + '_Start'
    df.insert(df.columns.get_loc(chromosome_col) + 1, 
              start_col, 
              df[new_species_col_name].map(species_data[species]['start']))

    # Strand column
    strand_col = species + '_Strand'
    df.insert(df.columns.get_loc(start_col) + 1, 
              strand_col, 
              df[new_species_col_name].map(species_data[species]['strand']))

# Sorting by chromosome columns
chromosome_columns = [species + '_Chromosome' for species in species_list]
df.sort_values(by=chromosome_columns, inplace=True)

# Write to a new CSV file
df.to_csv('ortholog_data_final_sorted.csv', index=False)
