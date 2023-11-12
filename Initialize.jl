include("DCJ.jl")
# Function that generates the initial population based on the 3 Genomes
function Initialize(G1::Vector{Int},G2::Vector{Int},G3::Vector{Int},num::Int)
    # Generate the Initial Population by performing the DCJ operation on the 3 pairs of Genomes and stopping at random points in the DCJ operation
    a=Int(floor(num/3))
    Population=Vector{Vector{Int}}(undef, num)
    for i in 1:a
        Population[i]=DCJ(G1,G2,true)
    end
    for i in 1:a
        Population[i+a]=DCJ(G2,G3,true)
    end
    for i in 1:Int(num-2*a)
        Population[i+2*a]=DCJ(G1,G3,true)
    end
    return Population
end