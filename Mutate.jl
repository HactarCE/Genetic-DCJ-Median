include("DCJdistance.jl")
include("DCJ.jl")
# Function that generates a mutation of a genome
function Mutate(Child::Vector{Int}, G1::Vector{Int}, G2::Vector{Int}, G3::Vector{Int})
    # Estimate the distances to each of the genomes
    d1_est=(DCJdistance(G1,G2)+DCJdistance(G1,G3)-DCJdistance(G2,G3))/2
    d2_est=(DCJdistance(G1,G2)+DCJdistance(G2,G3)-DCJdistance(G1,G3))/2
    d3_est=(DCJdistance(G1,G3)+DCJdistance(G2,G3)-DCJdistance(G1,G2))/2
    # Calculate the Actual distance of the median genome from the other genomes
    d1=DCJdistance(G1,Child)
    d2=DCJdistance(G2,Child)
    d3=DCJdistance(G3,Child)
    diff1=d1-d1_est
    diff2=d2-d2_est
    diff3=d3-d3_est
    # Mutate the median genome towards the genome it is furthest from
    if(diff1>=diff2&&diff1>=diff3)
        mut=DCJ(Child,G1,true)
    else
        if(diff2>=diff1&&diff2>=diff3)
            mut=DCJ(Child,G2,true)
        else
            mut=DCJ(Child,G3,true)
        end
    end
    return mut
end