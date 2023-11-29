include("Initialize.jl")
include("Crossover.jl")
include("SelectBest.jl")
function main(N::Int, G1::Vector{Vector{Int}}, G2::Vector{Vector{Int}}, G3::Vector{Vector{Int}}, PopSize::Int, NextGenSize::Int, NumOutputs::Int, NumIter::Int)
    Outputs=Dict()
    Population=Initialize(G1,G2,G3,PopSize)
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
        # Append the top genomes to the output list
        Outputs[iter]=Population[1:min(NumOutputs,length(Population))]
    end
    return Outputs
end


N=10                                # Number of Genes
G1=[[0,1,2,3,4,0],[6,7,-5,8,9,10]]  # Genome 1
G2=[[-2,-6,4],[0,1,7,8,9,5,10,3,0]] # Genome 2
G3=[[0,1,2,3,4,5,6,7,8,9,10,0]]     # Genome 3
PopSize=5                           # Size of the Population
NextGenSize=11                      # Size of the Next Generation
NumIter=10                          # Number of Iterations
NumOutputs=2                        # Number of Outputs per iteration
outputs=main(N,G1,G2,G3,PopSize,NextGenSize,NumOutputs,NumIter)
# outputs is a dictionary with iteration numbers as keys
for i in 1:NumIter
    println("Iteration ",i)
    for output in outputs[i]
        println(output)
    end
end
