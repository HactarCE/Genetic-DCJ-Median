import pandas as pd

# Load ortholog data from the CSV file
csv_file_path = 'ortholog_data.csv'
df = pd.read_csv(csv_file_path)

# Load the NCBI TSV file for gene name lookup
tsv_file_path = 'All_Invertebrates.gene_info'
print("Loading NCBI TSV file for lookup...")
lookup_df = pd.read_csv(tsv_file_path, sep='\t')

# Preprocess the NCBI TSV file to create a lookup dictionary
print("Creating lookup dictionary from NCBI TSV file...")
print("This may take a few minutes...")
lookup_dict = {}
for _, row in lookup_df.iterrows():
    species_prefix = row[3][:4]
    for gene_name in str(row[4]).split('|'):
        lookup_dict[(species_prefix, gene_name)] = row[2]
print("Lookup dictionary created.")

# Function to process gene names in columns 2, 3, 4, and 8
def process_gene_names(gene_name, species):
    if gene_name.startswith('LOC'):
        return gene_name

    # Remove characters up to and including the first "\"
    modified_gene_name = gene_name.split('\\', 1)[-1]
    
    # Further trim anything after a semicolon if present
    modified_gene_name = modified_gene_name.split(';', 1)[0]
    
    # Use the lookup dictionary
    return lookup_dict.get((species, modified_gene_name), gene_name)

# Apply modifications
print("Applying modifications to the ortholog data...")
df.iloc[:, 8] = df.iloc[:, 8].apply(lambda x: 'LOC' + x)
df.iloc[:, 1] = df.iloc[:, 1].apply(lambda x: process_gene_names(x, 'Dana'))
df.iloc[:, 2] = df.iloc[:, 2].apply(lambda x: process_gene_names(x, 'Dsim'))
df.iloc[:, 3] = df.iloc[:, 3].apply(lambda x: process_gene_names(x, 'Dyak'))
df.iloc[:, 7] = df.iloc[:, 7].apply(lambda x: process_gene_names(x, 'Dsec'))
df.iloc[:, 9] = df.iloc[:, 9].apply(lambda x: str(x).split(';')[-1] if ';' in str(x) else x)
print("Modifications applied.")

# Save the modified data to a new CSV file
output_file_path = 'ortholog_data_formatted.csv'
df.to_csv(output_file_path, index=False)

print("Ortholog data has been successfully modified and saved as", output_file_path)
