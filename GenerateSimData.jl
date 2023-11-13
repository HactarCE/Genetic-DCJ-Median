using Random

function perform_inversion!(genome)
    n = length(genome)
    i, j = rand(1:n, 2)
    if i > j
        i, j = j, i
    end
    # Reverse the segment and negate each element
    genome[i:j] = -reverse(genome[i:j])
end

function simulate_genomes(n, rates)
    base_genome = collect(1:n)
    genomes = []

    for rate in rates
        genome = copy(base_genome)
        for inversion in 1:rate
            perform_inversion!(genome)
        end
        push!(genomes, genome)
    end

    return base_genome, genomes
end

##################################################
# Testing the simulate_genomes function

# Length of the genomes
n = 10
# Inversion rates for the three genomes
rates = [5, 10, 15]

base_genome, genomes = simulate_genomes(n, rates)

println("Base genome:")
println(base_genome)

println("Simulated genomes:")
for genome in genomes
    println(genome)
end