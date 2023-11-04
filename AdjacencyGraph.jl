function AdjacencyGraph(G::Vector{Int})
    # G is the input Genome without telomeres
    
    # Initialize the AdjacencyGraph as a Vector of Vectors
    AG=Vector{Vector{Int}}()
    
    # If the size of the genome is one, it will have only two adjacencies
    if length(G)==1
        push!(AG,[0,-G[1]])
        push!(AG,[G[1],0])
        return AG
    end

    # Start with the first index and add the adjacencies to the AdjacencyGraph
    for i in 1:length(G)
        if i==1
            push!(AG,[0,-G[i]])    
        else
            push!(AG,[G[i-1],-G[i]])
            if i==length(G)
                push!(AG,[G[i],0])
            end
        end
    end
    return AG
end