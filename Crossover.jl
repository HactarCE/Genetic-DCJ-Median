include("Fitness.jl")
include("DCJ.jl")
include("Mutate.jl")
# Generate the Children after the crossover of two parents
function Crossover(N::Int,Parent1::Vector{Vector{Int}}, Parent2::Vector{Vector{Int}}, G1::Vector{Vector{Int}}, G2::Vector{Vector{Int}}, G3::Vector{Vector{Int}})
    # N is the number of Genes
    # Parent1, Parent2 are the two Genomes on whom the Crossover has to be performed
    # G1, G2, G3 are the input Genomes

    # Compute the fitness score for both the parents
    f1=Fitness(N,G1,G2,G3,Parent1)
    f2=Fitness(N,G1,G2,G3,Parent2)
    # Create one child by applying a random number of DCJ operations to the parent with the worse fitness score
    if(f1>f2)
        Child1=DCJ(Parent2,Parent1,rand(1:N))
        Child2=Parent1
    else
        Child1=DCJ(Parent1,Parent2,rand(1:N))
        Child2=Parent2
    end
    # Mutate both the generated children
    mut1=Mutate(N,Child1,G1,G2,G3)
    mut2=Mutate(N,Child2,G1,G2,G3)
    # Calculate the fitness scores of all the four genomes
    fc1=Fitness(N,G1,G2,G3,Child1)
    fc2=Fitness(N,G1,G2,G3,Child2)
    fmut1=Fitness(N,G1,G2,G3,mut1)
    fmut2=Fitness(N,G1,G2,G3,mut2)
    scores=[[fc1,Child1],[fc2,Child2],[fmut1,mut1],[fmut2,mut2]]
    sort!(scores)
    # return the best 2 children based on the fitness scores
    return scores[4][2], scores[3][2]
end

# Generate the Children after the crossover of two parents in the case of finding the Median of M Genomes
function CrossoverGeneral(N::Int,M::Int,Parent1::Vector{Vector{Int}}, Parent2::Vector{Vector{Int}}, Genomes::Vector{Vector{Vector{Int}}})
    # N is the Number of Genes
    # M is the Number of Input Genomes
    # Parent1, Parent2 are the two Genomes on whom the Crossover operation has to be performed
    # Genomes is the Vector of Input Genomes
    # Compute the fitness score for both the parents
    f1=FitnessGeneral(N,M,Genomes,Parent1)
    f2=FitnessGeneral(N,M,Genomes,Parent2)
    # Create one child by applying a random number of DCJ operations to the parent with the worse fitness score
    if(f1>f2)
        Child1=DCJ(Parent2,Parent1,rand(1:N))
        Child2=Parent1
    else
        Child1=DCJ(Parent1,Parent2,rand(1:N))
        Child2=Parent2
    end
    # Mutate both the generated children
    mut1=MutateGeneral(N,M,Child1,Genomes)
    mut2=MutateGeneral(N,M,Child2,Genomes)
    # Calculate the fitness scores of all the four genomes
    fc1=FitnessGeneral(N,M,Genomes,Child1)
    fc2=FitnessGeneral(N,M,Genomes,Child2)
    fmut1=FitnessGeneral(N,M,Genomes,mut1)
    fmut2=FitnessGeneral(N,M,Genomes,mut2)
    scores=[[fc1,Child1],[fc2,Child2],[fmut1,mut1],[fmut2,mut2]]
    sort!(scores)
    # return the best 2 children based on the fitness scores
    return scores[4][2], scores[3][2]
end