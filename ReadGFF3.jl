using CSV
using DataFrames
using Glob

root = "C:\\Users\\daily\\OneDrive\\Documents\\GitHub\\Genetic-DJC-Median"
homology_file = joinpath(root, "homology\\frb.GLEANR.TEremoved.clusters.tsv")

# Function to find GFF files for each genome in subdirectories of a root path
function find_gff_files(root_path::String)
    gff_filepaths = Dict()

    # Iterate over each subdirectory in the root directory
    for subdirectory in readdir(root_path, join=true)
        if isdir(subdirectory)
            # Use glob to find files that match the genome abbreviation pattern
            for file in glob("*.gff", subdirectory)
                genome_abbr = split(basename(file), "-")[1]
                if length(genome_abbr) == 4
                    gff_filepaths[genome_abbr] = joinpath(subdirectory, file)
                end
            end
        end
    end

    return gff_filepaths
end

gff_filepaths = find_gff_files(joinpath(root, "genome_annotations"))

println("Found GFF files for the following genomes:")
for (key, value) in gff_filepaths
    println("Key: $key, Value: $value")
end

# Function to extract single copy orthologs from homology file
function extract_single_copy_orthologs(filepath::String)
    # Read the TSV file
    df = CSV.read(filepath, DataFrame, delim='\t')

    # Filter rows where all species have a single copy ortholog !! (can be updated to only care about species we are working with) !!
    single_copy_rows = filter(row -> all(c -> c == '1', row.classification), df)

    # Create the dataframe with the needed columns
    single_copy_orthologs = single_copy_rows[:, [:cluster_id, :classification, :dana, :dere, :dgri, :dmoj, :dper, :dsec, :dvir, :dwil, :dyak]]

    return single_copy_orthologs
end

function parse_gff(gff_path::Any)
    # Read the GFF file
    df = CSV.read(gff_path, DataFrame, header=false, delim='\t')

    # Extract scaffold name and gene name
    scaffold_gene_mapping = Dict()
    for row in eachrow(df)
        scaffold = row[1]
        gene_info = split(row[9], ';')[1]
        gene_name = split(gene_info, '=')[2]
        gene_name = join(split(gene_name, "_")[1:3], "_")
        scaffold_gene_mapping[gene_name] = scaffold
    end
    return scaffold_gene_mapping
end

function find_ortholog_groups(homology_file::String, gff_filepaths::Dict{Any, Any})
    # Extract single copy orthologs
    single_copy_orthologs = extract_single_copy_orthologs(homology_file)

    # Parse the GFF files
    scaffold_gene_mappings = Dict()
    for (key, value) in gff_filepaths
        scaffold_gene_mappings[key] = parse_gff(value)
    end

    # Create a dictionary to store the ortholog groups by scaffold name within each species
    scaffold_to_orthologs = Dict()

    # Iterate over each ortholog group
    for row in eachrow(single_copy_orthologs)
        # Extract the gene names
        gene_names = row[3:end]

        # For each gene, extract the scaffold name and store the ortholog group
        for gene_name in gene_names
            genome_abbr = split(gene_name, "_")[1]
            if haskey(scaffold_gene_mappings[genome_abbr], gene_name)
                scaffold_name = scaffold_gene_mappings[genome_abbr][gene_name]
                if haskey(scaffold_to_orthologs, scaffold_name)
                    push!(scaffold_to_orthologs[scaffold_name], gene_names)
                else
                    scaffold_to_orthologs[scaffold_name] = [gene_names]
                end
            end
        end
    end

    # Create a dataframe to store the sets of ortholog groups that share the same scaffold within each species
    df_out = DataFrame(scaffold_name = String[], ortholog_groups = String[])

    # Iterate over each scaffold
    for (scaffold_name, ortholog_groups) in scaffold_to_orthologs
        # If the scaffold contains more than one ortholog group, store the scaffold and its ortholog groups
        if length(ortholog_groups) > 1
            ortholog_groups_str = join([join(gene_group, ", ") for gene_group in ortholog_groups], "; ")
            push!(df_out, (scaffold_name = scaffold_name, ortholog_groups = ortholog_groups_str))
        end
    end

    return df_out
end

ortholog_groups = find_ortholog_groups(homology_file, gff_filepaths)

println(ortholog_groups)