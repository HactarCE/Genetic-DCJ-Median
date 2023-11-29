include("Fitness.jl")
include("Helper.jl")
# Function that eliminates duplicated Genomes from the Population
function EliminateDuplicates!(Population::Vector{Vector{Vector{Int}}})
    # Population is the Population of all Genomes, generated from the Crossover and Mutation operations
  
    # Start with the first Genome
    i=1
    while(i<=length(Population))
      j=i+1
      # For each other Genomes check if it is equal to the ith Genome
      while(j<=length(Population))
        # If it is equal delete it from the population
        if sameGenome(Population[i],Population[j])
          deleteat!(Population,j)
        else
          j+=1
        end
      end
      i+=1
    end
  end

# Function that selects best from a given population
function SelectBest(N::Int,Population::Vector{Vector{Vector{Int}}},pop_size::Int,G1::Vector{Vector{Int}},G2::Vector{Vector{Int}},G3::Vector{Vector{Int}})
    # N is the number of Genes
    # Population is the Population of Genomes generated from the crossover and mutation operations
    # pop_size is the size of the population
    # G1, G2, G3 are the input Genomes

    # Eliminate duplicates from the Population
    EliminateDuplicates!(Population)
    # Initialize vector of scores
    scores=[]
    # For each genome in the population compute the fitness score
    for i in 1:length(Population)
        fit=Fitness(N,G1,G2,G3,Population[i])
        append!(scores,[[fit,i]])
    end
    # Sort the scores in descending order
    sort!(scores, rev=true)
    # Separted the top 10% of scores from the remaining
    top_scores=scores[1:min(Int(round(0.1*pop_size)),length(Population))]
    remaining_scores=scores[1+Int(round(0.1*pop_size)):end]
    # Get the indices of the top scores and the remaining scores
    top_indices=[]
    for score in top_scores
      append!(top_indices,Int(score[2]))
    end
    remaining_indices=[]
    for score in remaining_scores
      append!(remaining_indices,Int(score[2]))
    end
    # Automatically choose the first 10 percent
    NewPopulation=Population[top_indices]
    # From the remaining population randomly choose candidate genomes
    RemainingPopulation=Population[remaining_indices]
    for i in length(NewPopulation)+1:pop_size
        if(length(RemainingPopulation)==0)
          break
        end
        if(length(RemainingPopulation)>1)
          r=rand(1:length(RemainingPopulation))
        else
          r=1
        end
        push!(NewPopulation,RemainingPopulation[r])
        deleteat!(RemainingPopulation,r)
    end
    return NewPopulation
end

# Function that selects best from a given population in the case of finding the Median of M Genomes
function SelectBestGeneral(N::Int,M::Int,Population::Vector{Vector{Vector{Int}}},pop_size::Int,Genomes::Vector{Vector{Vector{Int}}})
  # N is the Number of Genes
  # M is the Number of Input Genomes
  # Population is the Population Generated from the Crossover and Mutation Operations
  # pop_size is the Size of the Population
  # Genomes is the Vector of Input Genomes

  # Eliminate duplicated Genomes in the Population
  EliminateDuplicates!(Population)
  # Initialize vector of scores
  scores=[]
  # For each genome in the population compute the fitness score
  for i in 1:length(Population)
      fit=FitnessGeneral(N,M,Genomes,Population[i])
      append!(scores,[[fit,i]])
  end
  # Sort the scores in descending order
  sort!(scores, rev=true)
  # Separate the top 10% of scores from the remaining scores
  if length(Population)<Int(round(0.1*pop_size))
    top_scores=scores
  else
    top_scores=scores[1:Int(round(0.1*pop_size))]
  end
  remaining_scores=scores[max(1,Int(round(0.1*pop_size))):end]
  # Get the indices of the top 10% and remaining scores
  top_indices=[]
  for score in top_scores
    append!(top_indices,Int(score[2]))
  end
  remaining_indices=[]
  for score in remaining_scores
    append!(remaining_indices,Int(score[2]))
  end
  # Automatically choose the first 10 percent
  NewPopulation=Population[top_indices]
  # From the remaining population randomly choose candidate genomes
  RemainingPopulation=Population[remaining_indices]
  for i in length(NewPopulation):pop_size
      if(length(RemainingPopulation)==0)
        break
      end
      if(length(RemainingPopulation)>1)
        r=rand(1:length(RemainingPopulation))
      else
        r=1
      end
      push!(NewPopulation,RemainingPopulation[r])
      deleteat!(RemainingPopulation,r)
  end
  return NewPopulation
end