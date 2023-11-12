# Generate the Adjacency Graph from the Genome
function AdjacencyGraph(G::Vector{Int})
    # G is the input Genome with telomeres
    
    # Initialize the AdjacencyGraph as a Vector of Vectors
    AG=Vector{Vector{Int}}()

    # Start with the first index and add the adjacencies to the AdjacencyGraph
    start=1
    while(start<=length(G))
        # Look for the next telomere it will either be the start or end of a gene
        j=findnext(==(0),G,start+1)
        if isnothing(j)
            j=length(G)+1
        end
        # if the start is not a telomere the gene is circular
        if G[start]!=0
            j=j-1
        end
        # add the adjacencies to the AdjacencyGraph
        for i in start:j
            if i==start
                if G[i]!=0
                    push!(AG,[G[j],-G[i]])
                end
            else
                push!(AG,[G[i-1],-G[i]])
            end
        end
        start=j+1
    end
    return AG
end