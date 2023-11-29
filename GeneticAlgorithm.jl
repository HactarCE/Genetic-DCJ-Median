include("Crossover.jl")
include("Initialize.jl")
include("SelectBest.jl")
# Function that performs the Genetic Algorithm
function GeneticAlgorithm(N::Int,G1::Vector{Vector{Int}},G2::Vector{Vector{Int}},G3::Vector{Vector{Int}},PopSize::Int,NextGenSize::Int,NumIter::Int,tol::Int=NumIter,store_iteration_result::Bool=false)
    # N is the number of Genes
    # G1, G2, G3 are the input Genomes
    # PopSize the Size of the Population
    # NextGenSize is the Size of the Generated Next Generation, should be an even number because children are generated in pairs
    # NumIter is the number of iterations
    # tol is the number of iterations for which no improvement in the best score will be tolerated

    # Initialize the algorithm
    Population=Initialize(G1,G2,G3,PopSize)
    # In each iteration
    iter_required=NumIter
    prev_best=0
    num=0
    NextGenSize+=1
    if store_iteration_result
      results=zeros(div(NumIter,10))
    end
    for iter in 1:NumIter
        NewPopulation=Vector{Vector{Vector{Int}}}(undef, NextGenSize)
        # The Best Genome is automatically selected in the Next Generation
        NewPopulation[1]=deepcopy(Population[1])
        j=2
        # Generate the new population by performing the crossover and mutation by choosing two random parents
        while(true)
            if(j>NextGenSize)
              break
            end
            child1,child2=Crossover(N,Population[rand(1:length(Population))],Population[rand(1:length(Population))],G1,G2,G3)
            NewPopulation[j]=child1
            j+=1
            if(j>NextGenSize)
              break
            end
            NewPopulation[j]=child2
            j+=1
        end
        # Select the best from the population
        Population=SelectBest(N,NewPopulation,PopSize,G1,G2,G3)
        curr_score=Fitness(N,G1,G2,G3,Population[1])
        if store_iteration_result&&iter%10==0
          results[div(iter,10)]=curr_score
        end
        # If the current is the best possible score return
        if curr_score==N
          iter_required=iter
          break
        end
        # If the current score has not improved after 'tol' number of iterations break
        if curr_score<=prev_best
          num+=1
          if num==tol
            iter_required=iter
            break
          end
        else
          num=0
        end
        prev_best=max(prev_best,curr_score)
    end
    if store_iteration_result&&iter_required<NumIter
      for i in div(iter_required,10)+1:div(NumIter,10)
        results[i]=N
      end
    end
    if store_iteration_result
      return Population[1],iter_required,results
    else
      return Population[1],iter_required
    end
end

# Function that performs the Genetic Algorithm in the General case of M Genomes
function GeneticAlgorithmGeneral(N::Int,M::Int,Genomes::Vector{Vector{Vector{Int}}},PopSize::Int,NextGenSize::Int,NumIter::Int,tol::Int=NumIter)
  # N is the Number of Genes
  # M is the Number of Input Genomes
  # Genomes is the Vector of Input Genomes
  # PopSize is the Size of the Population
  # NextGenSize is the Size of the Generated Next Generation
  # NumIter is the Number of Iterations
  # tol is the number of iteration for which no improvement in the best score is tolerated

  # Initialize the algorithm
  Population=InitializeGeneral(M,Genomes,PopSize)
  # In each iteration
  iter_required=NumIter
  prev_best=0
  num=0
  for iter in 1:NumIter
      NewPopulation=Vector{Vector{Vector{Int}}}(undef, NextGenSize+1)
      NewPopulation[end]=deepcopy(Population[1])
      # Generate the new population by performing the crossover and mutation by choosing two random parents
      for i in 1:div(NextGenSize,2)
          child1,child2=CrossoverGeneral(N,M,Population[rand(1:length(Population))],Population[rand(1:length(Population))],Genomes)
          NewPopulation[2*i-1]=child1
          NewPopulation[2*i]=child2
      end
      # Select the best from the population
      Population=SelectBestGeneral(N,M,NewPopulation,PopSize,Genomes)
      curr_score=FitnessGeneral(N,M,Genomes,Population[1])
      # If the current is the best possible score return
      if curr_score==N
        iter_required=iter
        break
      end
      # If the current score has not improved after a certain number of iterations break
      if curr_score<=prev_best
        num+=1
        if num==tol
          iter_required=iter
          break
        end
      else
        num=0
      end
      prev_best=max(prev_best,curr_score)
  end
  return Population[1],iter_required
end