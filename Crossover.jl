include("Fitness.jl")
include("DCJ.jl")
include("Mutate.jl")
# Generate the Children after the crossover of two parents
function Crossover(Parent1::Vector{Int}, Parent2::Vector{Int}, G1::Vector{Int}, G2::Vector{Int}, G3::Vector{Int})
    # Compute the fitness score for both the parents
    f1=Fitness(G1,G2,G3,Parent1)
    f2=Fitness(G1,G2,G3,Parent2)
    # Create one child by applying a random number of DCJ operations to the parent with the worse fitness score
    if(f1>f2)
        Child1=DCJ(Parent2,Parent1,true)
        Child2=Parent1
    else
        Child1=DCJ(Parent1,Parent2,true)
        Child2=Parent2
    end
    # Mutate both the generated children
    mut1=Mutate(Child1,G1,G2,G3)
    mut2=Mutate(Child2,G1,G2,G3)
    # Calculate the fitness scores of all the four genomes
    fc1=Fitness(G1,G2,G3,Child1)
    fc2=Fitness(G1,G2,G3,Child2)
    fmut1=Fitness(G1,G2,G3,mut1)
    fmut2=Fitness(G1,G2,G3,mut2)
    scores=[[fc1,Child1],[fc2,Child2],[fmut1,mut1],[fmut2,mut2]]
    sort!(scores)
    # return the best 2 children based on the fitness scores
    return scores[4][2], scores[3][2]
end