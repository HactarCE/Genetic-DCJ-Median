include("DCJ.jl")
# Function that generates the initial population based on the 3 Genomes
function Initialize(G1::Vector{Vector{Int}},G2::Vector{Vector{Int}},G3::Vector{Vector{Int}},pop_size::Int)
    # G1, G2, G3 are the input Genomes
    # pop_size is the Size of the Population

    # Generate the Initial Population by performing the DCJ operation on the 3 pairs of Genomes and stopping at random points in the DCJ operation
    a=div(pop_size,3)
    Population=Vector{Vector{Vector{Int}}}(undef, pop_size)
    for i in 1:a
        Population[i]=DCJ(G1,G2,rand(1:N))
    end
    for i in 1:a
        Population[i+a]=DCJ(G2,G3,rand(1:N))
    end
    for i in 1:pop_size-2*a
        Population[i+2*a]=DCJ(G1,G3,rand(1:N))
    end
    return Population
end

# Function that generates the initial population based on M Genomes
function InitializeGeneral(M::Int,Genomes::Vector{Vector{Vector{Int}}},pop_size::Int)
    # M is the number of Input Genomes
    # Genomes is the Vector of Input Genomes
    # pop_size is the Size of the Population

    # Generate the Initial Population by performing the DCJ operation on all pairs of Genomes and stopping at random points in the DCJ operation
    num_pairs=M*(M-1)/2
    a=Int(floor(pop_size/num_pairs))
    Population=Vector{Vector{Vector{Int}}}(undef, pop_size)
    k=1
    for i in 1:M-2
      for j in i+1:M
        for _ in 1:a
          Population[k]=DCJ(Genomes[i],Genomes[j],rand(1:N))
          k+=1
        end
      end
    end
    for _ in 1:Int(pop_size-(num_pairs-1)*a)
      Population[k]=DCJ(Genomes[end-1],Genomes[end],rand(1:N))
      k+=1
    end
    return Population
end