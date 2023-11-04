include("DCJdistance.jl")
# Function to compute the fitness score based on the DCJ distance
function Fitness(G1::Vector{Int}, G2::Vector{Int}, G3::Vector{Int}, G::Vector{Int})
    N=length(G1)
    # SBest is the best possible median score
    SBest=ceil((DCJdistance(G1,G2)+DCJdistance(G2,G3)+DCJdistance(G1,G3))/2)
    # S is the median score of genome G
    S=DCJdistance(G1,G)+DCJdistance(G2,G)+DCJdistance(G3,G)
    # FG is the fitness score of Genome G
    FG=N-(S-SBest)
    return FG
end