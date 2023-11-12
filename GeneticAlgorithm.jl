using DelimitedFiles
include("Crossover.jl")
include("Initialize.jl")
include("SelectBest.jl")
# Function that performs the Genetic Algorithm
function GeneticAlgorithm(G1::Vector{Int},G2::Vector{Int},G3::Vector{Int},PopSize::Int,NextGenSize::Int,NumIter::Int)
    # Initialize the algorithm
    Population=Initialize(G1,G2,G3,PopSize)
    # Clear previous file contents
    file=open("output.txt","w")
    close(file)
    # Open output file
    file=open("output.txt","a")
    # In each iteration
    for iter in 1:NumIter    
        NewPopulation=Vector{Vector{Int}}(undef, NextGenSize)
        # Generate the new population by performing the crossover and mutation by choosing two random parents
        for i in 1:Int(NextGenSize/2)
            child1,child2=Crossover(Population[rand(1:PopSize)],Population[rand(1:PopSize)],G1,G2,G3)
            NewPopulation[2*i-1]=child1
            NewPopulation[2*i]=child2
        end
        # Select the best from the population
        Population=SelectBest(NewPopulation,PopSize,G1,G2,G3)
        write(file,"Iteration"*string(iter)*"\n")
        # Write the top 10 genomes to the output file
        writedlm(file,Population[1:10])
    end
    close(file)
    return Population[1]
end