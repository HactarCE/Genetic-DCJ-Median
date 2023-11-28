include("DCJdistance.jl")
# Function to compute the fitness score based on the DCJ distance
function Fitness(N::Int,G1::Vector{Vector{Int}}, G2::Vector{Vector{Int}}, G3::Vector{Vector{Int}}, G::Vector{Vector{Int}})
    # N is the number of Genes
    # G1, G2, G3 are the input Genomes
    # G is the candidate Median Genome

    # SBest is the best possible median score
    SBest=ceil((DCJdistance(N,G1,G2)+DCJdistance(N,G2,G3)+DCJdistance(N,G1,G3))/2)
    # S is the median score of genome G
    S=DCJdistance(N,G1,G)+DCJdistance(N,G2,G)+DCJdistance(N,G3,G)
    # FG is the fitness score of Genome G
    FG=N-(S-SBest)
    return FG
end

# Function to compute the fitness score based on the DCJ distance for the Median of M Genomes
function FitnessGeneral(N::Int,M::Int,Genomes::Vector{Vector{Vector{Int}}}, G::Vector{Vector{Int}})
    # N is the Number of Genomes
    # M is the Number of Input Genomes
    # Genomes is the Vector of Input Genomes
    # G is the Candidate Median Genome

    # SBest is the best possible median score
    SBest=0
    for i in 1:M-1
      for j in i+1:M
        SBest+=DCJdistance(N,Genomes[i],Genomes[j])
      end
    end
    SBest=ceil(SBest/(M-1))
    # S is the median score of genome G
    S=0
    for i in 1:length(Genomes)
      S+=DCJdistance(N,Genomes[i],G)
    end
    # FG is the fitness score of Genome G
    FG=N-(S-SBest)
    return FG
end