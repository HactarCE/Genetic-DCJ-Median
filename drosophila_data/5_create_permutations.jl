using CSV
using DataFrames
cd(@__DIR__)

# Function to read the CSV file and process it
function process_csv(file_path::String)
    # Read the CSV file
    df = CSV.read(file_path, DataFrame)

    # Add a unique identifier to each row
    df[!, :OrthologID] = 1:nrow(df)

    # Identify chromosome columns (assuming they contain the word 'Chromosome')
    chromosome_cols = [name for name in names(df) if occursin("Chromosome", String(name))]

    # Group data by the combination of chromosomes
    grouped = groupby(df, chromosome_cols)

    # Create separate tables for each chromosome combination, sort by size, and then by D. melanogaster start position
    tables = sort([sort(subgroup[:, Not(chromosome_cols)], :Dmelanogaster_Start) for subgroup in grouped], by=x -> nrow(x), rev=true)

    return tables
end

# Function to print ortholog group counts for the top chromosome fragments
function print_top_group_counts(tables::Array{DataFrame,1})
    num_tables = length(tables)
    num_to_display = min(num_tables, 50)

    println("Ortholog Group Counts for the Top ", num_to_display, " Chromosome Fragments:")
    for i in 1:num_to_display
        println("Fragment ", i, ": ", nrow(tables[i]), " ortholog groups")
    end
end

# Function to create a dictionary of signed permutations grouped by species
function create_permutations(tables::Array{DataFrame,1}, min_ortholog_groups::Int=0)
    species_permutations = Dict()

    for (table_idx, table) in enumerate(tables)
        # Skip tables with fewer ortholog groups than the specified threshold
        if nrow(table) < min_ortholog_groups
            continue
        end

        for col in names(table)
            if occursin("_Gene", col)
                species = split(col, "_")[1]
                start_col = species * "_Start"
                strand_col = species * "_Strand"

                # Sort based on start positions
                sorted_genes = sortperm(table[!, start_col])

                # Determine sign based on strand and use unique numbering for genes on each chromosome fragment
                signed_perm = [table[i, strand_col] == "+" ? table[i, :OrthologID] : -table[i, :OrthologID] for i in sorted_genes]

                # Update the species_permutations dictionary
                if !haskey(species_permutations, species)
                    species_permutations[species] = []
                end
                push!(species_permutations[species], signed_perm)
            end
        end
    end

    return species_permutations
end

# CSV file must be in the same directory as this script
tables = process_csv("ortholog_data_final_sorted.csv")

# Print ortholog group counts for the top groups
print_top_group_counts(tables)

# Create signed permutations for each species in each table
permutations = create_permutations(tables, 50)

println(permutations["Dmelanogaster"][1])