using CSV
using DataFrames
cd(@__DIR__)

# Function to read the CSV file and process it
function process_csv(file_path::String)
    # Read the CSV file
    df = CSV.read(file_path, DataFrame)

    # Identify chromosome columns (assuming they contain the word 'Chromosome')
    chromosome_cols = [name for name in names(df) if occursin("Chromosome", String(name))]

    # Group data by the combination of chromosomes
    grouped = groupby(df, chromosome_cols)

    # Create separate tables for each chromosome combination, sort by size, and then by D. melanogaster start position
    tables = sort([sort(subgroup[:, Not(chromosome_cols)], :Dmelanogaster_Start) for subgroup in grouped], by=x -> nrow(x), rev=true)

    return tables
end

# Function to print ortholog group counts for the top groups
function print_top_group_counts(tables::Array{DataFrame,1})
    num_tables = length(tables)
    num_to_display = min(num_tables, 50)

    println("Ortholog Group Counts for the Top ", num_to_display, " Groups:")
    for i in 1:num_to_display
        println("Group ", i, ": ", nrow(tables[i]), " ortholog groups")
    end
end

# Function to create signed permutations for each species in each table
function create_signed_permutations(tables::Array{DataFrame,1})
    permutations = []

    for table in tables
        species_permutations = Dict()
        for col in names(table)
            if occursin("_Gene", col)
                species = split(col, "_")[1]
                start_col = species * "_Start"
                strand_col = species * "_Strand"

                # Sort based on start positions
                sorted_genes = sortperm(table[!, start_col])

                # Determine sign based on strand
                signed_perm = [table[i, strand_col] == "+" ? j : -j for (i, j) in enumerate(sorted_genes)]

                species_permutations[species] = signed_perm
            end
        end
        push!(permutations, species_permutations)
    end

    return permutations
end

tables = process_csv("ortholog_data_final_sorted.csv")

# Print ortholog group counts for the top groups
print_top_group_counts(tables)

# Create signed permutations for each species in each table
permutations = create_signed_permutations(tables)

println(permutations[1])