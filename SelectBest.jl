include("Fitness.jl")
# Function that selects best from a given population
function SelectBest(Population::Vector{Vector{Int}},k::Int,G1::Vector{Int},G2::Vector{Int},G3::Vector{Int})
    # Initialize vector of scores
    scores=[]
    # For each genome in the population compute the fitness score
    for i in 1:length(Population)
        fit=Fitness(G1,G2,G3,Population[i])
        append!(scores,[[fit,i]])
    end
    # Sort the scores in descending order
    sort!(scores, rev=true)
    # Automatically choose the first 10 percent
    NewPopulation=Population[1:Int(round(0.1*length(Population)))]
    # From the remaining population randomly choose candidate genomes
    RemainingPopulation=Population[max(1,Int(round(0.1*length(Population)))):length(Population)-1]
    for i in length(NewPopulation):k-1
        r=rand(1:length(RemainingPopulation))
        push!(NewPopulation,RemainingPopulation[r])
    end
    return NewPopulation
end