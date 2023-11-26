import requests
import time
import csv

# Constants
BASE_URL = "https://data.orthodb.org/current"
SEARCH_URL = f"{BASE_URL}/search?singlecopy=1&universal=1&level=7215&species=7217_0%2C7226_0%2C7227_0%2C7229_0%2C129105_0%2C7238_0%2C7240_0%2C28584_0%2C7245_0&take=30000"
TAB_URL = f"{BASE_URL}/tab?id="
SPECIES_OF_INTEREST = [
    "Drosophila ananassae", "Drosophila simulans", "Drosophila yakuba", "Drosophila suzukii",
    "Drosophila miranda", "Drosophila mauritiana", "Drosophila sechellia",
    "Drosophila santomea", "Drosophila melanogaster"
]

def get_ortholog_group_ids():
    response = requests.get(SEARCH_URL)
    time.sleep(1.2)  # Wait after the search request
    data = response.json()
    return data['data']

def get_gene_data(ortholog_group_id):
    response = requests.get(TAB_URL + ortholog_group_id)
    return response.text

def parse_tsv_data(tsv_data, ortholog_group_id):
    reader = csv.DictReader(tsv_data.splitlines(), delimiter='\t')
    gene_names = {}
    for row in reader:
        species = row['organism_name']
        gene_name = row['pub_gene_id']
        if species in SPECIES_OF_INTEREST:
            if species in gene_names:
                print(f"Warning: Multiple genes found for species {species} in ortholog group {ortholog_group_id}. Skipping this group.")
                return None
            gene_names[species] = gene_name

    # Check for species with no genes found
    for species in SPECIES_OF_INTEREST:
        if species not in gene_names:
            print(f"Warning: No gene found for species {species} in ortholog group {ortholog_group_id}. Skipping this group.")
            return None

    return gene_names

def main():
    print("Starting the data extraction process...")
    ortholog_group_ids = get_ortholog_group_ids()
    results = []
    skipped = 0
    total = len(ortholog_group_ids)

    for i, ortholog_group_id in enumerate(ortholog_group_ids, 1):
        print(f"Processing ortholog group {i} of {total} (ID: {ortholog_group_id})...")
        tsv_data = get_gene_data(ortholog_group_id)
        gene_names = parse_tsv_data(tsv_data, ortholog_group_id)
        if gene_names:
            row = [ortholog_group_id] + [gene_names[species] for species in SPECIES_OF_INTEREST]
            results.append(row)
        else:
            skipped += 1
        time.sleep(1.2)  # To respect the rate limit

    # Writing results to a CSV file
    with open('ortholog_data.csv', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['Ortholog Group ID'] + SPECIES_OF_INTEREST)
        writer.writerows(results)

    print(f"Data extraction completed. Total ortholog groups processed: {total}, skipped: {skipped}.")

if __name__ == "__main__":
    main()
