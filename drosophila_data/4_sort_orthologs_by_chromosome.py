import pandas as pd
import gzip
import os
import glob

def parse_gff3(file_name):
    with gzip.open(file_name, 'rt') as file:
        gene_to_chromosome = {}
        for line in file:
            if not line.startswith('#'):
                parts = line.split('\t')
                if parts[2] == "gene":
                    chromosome = parts[0]
                    attributes = parts[8]
                    gene_info = [attr for attr in attributes.split(';') if attr.startswith('Name=') or attr.startswith('gene_synonym=')]
                    for info in gene_info:
                        key, value = info.split('=')
                        genes = value.split(',')
                        for gene in genes:
                            gene_to_chromosome[gene] = chromosome
    return gene_to_chromosome

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

# Create species-specific gene-to-chromosome dictionaries
species_gene_to_chromosome = {species: parse_gff3(file) for species, file in gff3_files.items()}

csv_file = os.path.join("orthodb_homology_info", "filtered_ortholog_data_formatted.csv")

# Read the existing CSV file
df = pd.read_csv(csv_file)

# Rename gene columns and add chromosome columns
for species in gff3_files.keys():
    species_col_name = (species[0] + "rosophila " + species[1:])
    new_species_col_name = species + "_Gene"
    df.rename(columns={species_col_name: new_species_col_name}, inplace=True)

    chromosome_col = species + '_Chromosome'
    chromosome_data = df[new_species_col_name].map(species_gene_to_chromosome[species])
    insert_loc = df.columns.get_loc(new_species_col_name) + 1
    df.insert(insert_loc, chromosome_col, chromosome_data)

# Sorting by chromosome columns
chromosome_columns = [species + '_Chromosome' for species in species_list]
df.sort_values(by=chromosome_columns, inplace=True)

# Write to a new CSV file
df.to_csv('ortholog_data_final_sorted.csv', index=False)
