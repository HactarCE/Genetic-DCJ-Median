# Generate the Adjacency Graph from the Genome
function AdjacencyGraph(G::Vector{Vector{Int}})
  # G is the input Genome with each chromosome represented as a separate vector

  # Initialize the AdjacencyGraph as a Vector of Vectors
  AG=Vector{Vector{Int}}()

  for chrom in G
    chrom_length=length(chrom)
    for j in 2:chrom_length
      push!(AG,[chrom[j-1],-chrom[j]])
    end
    if chrom[end]!=0
      push!(AG,[chrom[end],-chrom[1]])
    end
  end
  return AG
end