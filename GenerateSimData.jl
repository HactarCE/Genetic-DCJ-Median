using Random

function perform_inversion!(genome)
    n = length(genome)
    i, j = rand(1:n, 2)
    if i > j
        i, j = j, i
    end
    genome[i:j] = -reverse(genome[i:j])
end

function simulate_genomes(n, rates, output_file, seed=nothing)
    # Set the seed if provided
    if seed !== nothing
        Random.seed!(seed)
    end

    base_genome = collect(1:n)
    genomes = []

    for rate in rates
        genome = copy(base_genome)
        for inversion in 1:rate
            perform_inversion!(genome)
        end
        push!(genomes, genome)
    end

    # Write to file
    open(output_file, "w") do file
        for genome in genomes
            write(file, join(genome, " "), "\n")
        end
    end

    return base_genome, genomes
end

##################################################
# Testing the simulate_genomes function

# Length of the genomes
n = 10
# Inversion rates for the three genomes
rates = [5, 10, 15]
# Seed for reproducability
seed = 12

base_genome, genomes = simulate_genomes(n, rates, "simulated_genomes.txt", seed)

println("Base genome:")
println(base_genome)

println("Simulated genomes:")
for genome in genomes
    println(genome)
end