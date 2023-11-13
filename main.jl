include("Crossover.jl")
include("AdjacencyGraph.jl")
include("Genome.jl")
include("Initialize.jl")
include("SelectBest.jl")
include("GeneticAlgorithm.jl")
include("Input.jl")
# Read Input from the input file
G1,G2,G3=Input("input.txt")
G1=Vector{Int}(G1)
G2=Vector{Int}(G2)
G3=Vector{Int}(G3)
# Perform the Genetic Algorithm
out=GeneticAlgorithm(G1,G2,G3,100,200,100)
# Print the best median genome
println(out)