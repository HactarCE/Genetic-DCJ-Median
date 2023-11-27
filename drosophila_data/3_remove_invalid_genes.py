import pandas as pd
import gzip
import os
import glob

def read_gff3(gff3_file):
    with gzip.open(gff3_file, 'rt') as file:
        genes = {}
        for line in file:
            if not line.startswith("#"):
                parts = line.strip().split("\t")
                if parts[2] == "gene":
                    info = parts[8].split(";")
                    name = [i.split("=")[1] for i in info if i.startswith("Name=")][0]
                    synonyms = [i.split("=")[1].split(",") for i in info if i.startswith("gene_synonym=")]
                    synonyms = synonyms[0] if synonyms else []
                    genes[name] = 1
                    for synonym in synonyms:
                        genes[synonym] = 1
        return genes

def validate_genes(csv_file, gff3_files):
    df = pd.read_csv(csv_file)
    original_row_count = df.shape[0]
    species_columns = df.columns[1:]
    gene_data = {species: read_gff3(gff3_files[(species[0]+species[11:])]) for species in species_columns}

    valid_rows = []
    for index, row in df.iterrows():
        valid = True
        for species in species_columns:
            if row[species] not in gene_data[species]:
                valid = False
                break
        if valid:
            valid_rows.append(index)

    updated_df = df.loc[valid_rows]
    updated_df.to_csv(os.path.join("orthodb_homology_info", "filtered_ortholog_data_formatted.csv"), index=False)

    print(f"Original rows: {original_row_count}")
    print(f"Rows after removal: {updated_df.shape[0]}")
    print(f"Total rows removed: {original_row_count - updated_df.shape[0]}")

def find_gff_path(species):
    # Construct a pattern to match the folder names
    pattern = os.path.join(species, "ncbi_dataset", "data", "GCF_*", "genomic.gff.gz")
    # Use glob to find matching paths
    matching_paths = glob.glob(pattern)
    if matching_paths:
        return matching_paths[0]
    else:
        raise FileNotFoundError(f"No GFF file found for {species}!")

csv_file = os.path.join("orthodb_homology_info", "ortholog_data_formatted.csv")

species_list = [
    "Dananassae", "Dsimulans", "Dyakuba", "Dsuzukii", "Dmiranda",
    "Dmauritiana", "Dsechellia", "Dsantomea", "Dmelanogaster"
]

gff3_files = {species: find_gff_path(species) for species in species_list}

validate_genes(csv_file, gff3_files)
