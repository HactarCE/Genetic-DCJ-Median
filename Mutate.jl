include("DCJdistance.jl")
include("DCJ.jl")
# Function that generates a mutation of a genome
function Mutate(N::Int,Child::Vector{Vector{Int}}, G1::Vector{Vector{Int}}, G2::Vector{Vector{Int}}, G3::Vector{Vector{Int}})
    # N is the number of Genes
    # Child is the Genome that needs to be mutated
    # G1, G2, G3 are the input Genomes

    # Estimate the distances to each of the genomes
    d1_est=div((DCJdistance(N,G1,G2)+DCJdistance(N,G1,G3)-DCJdistance(N,G2,G3)),2)
    d2_est=div((DCJdistance(N,G1,G2)+DCJdistance(N,G2,G3)-DCJdistance(N,G1,G3)),2)
    d3_est=div((DCJdistance(N,G1,G3)+DCJdistance(N,G2,G3)-DCJdistance(N,G1,G2)),2)
    # Calculate the Actual distance of the median genome from the other genomes
    d1=DCJdistance(N,G1,Child)
    d2=DCJdistance(N,G2,Child)
    d3=DCJdistance(N,G3,Child)
    diff1=d1-d1_est
    diff2=d2-d2_est
    diff3=d3-d3_est
    # Mutate the median genome towards the genome it is furthest from
    if(diff1>=diff2&&diff1>=diff3)
        mut=DCJ(Child,G1,diff1)
    else
        if(diff2>=diff1&&diff2>=diff3)
            mut=DCJ(Child,G2,diff2)
        else
            mut=DCJ(Child,G3,diff3)
        end
    end
    return mut
end

# Function that generates a mutation of a genome in the case of Median of M Genomes
function MutateGeneral(N::Int,M::Int,Child::Vector{Vector{Int}}, Genomes::Vector{Vector{Vector{Int}}})
    # N is the Number of Genes
    # M is the Number of Input Genomes
    # Child is the Genome that has to be mutated
    # Genomes is the Vector of Input Genomes

    # Estimate the distances to each of the genomes
    # d_est[i]=(sum of distances of Genome i to all other genomes - sum of pair wise distances of all other genomes)/(M-1)
    d_est=Vector{Int}(undef,length(Genomes))
    for i in 1:M-1
      for j in i+1:M
        dist=DCJdistance(N,Genomes[i],Genomes[j])
        for k in 1:M
          if k==i||k==j
            d_est[k]+=dist
          else
            d_est[k]-=dist
          end
        end
      end
    end
    for i in 1:M
      d_est[i]=div(d_est[i],(M-1))
    end
    # Calculate the Actual distance of the median genome from the other genomes
    d=Vector{Int}(undef,M)
    for i in 1:M
      d[i]=DCJdistance(N,Genomes[i],Child)
    end
    diff=d-d_est
    # Mutate the median genome towards the genome it is furthest from
    idx=argmax(diff)
    mut=DCJ(Child,Genomes[idx],diff[idx])
    return mut
end