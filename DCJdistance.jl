include("AdjacencyGraph.jl")
# Function that recursively computes the length of a path
function ComputePathLength(E1::Vector{Vector{Int}}, E2::Vector{Vector{Int}}, l::Int, i::Int, select::Int)
    # E1 and E2 store the edges of the AdjacencyGraph
    # l is the current path length
    # i is the index of the current edge
    # If select is 1 use the first genome otherwise use the second
    if select==1
        # Store the last visited node, to check for a cycle
        last=E2[i]
        # Increment the length of the path
        l=l+1
        # Delete the used edges
        deleteat!(E1,i)
        deleteat!(E2,i)
        # Find the next edge
        j=findfirst(elt->last==elt,E2)
        # If an edge exist recursively call the function with the new edge, otherwise return
        if(!(j isa Nothing))
            last,s,l=ComputePathLength(E1,E2,l,j,2)
        else
            return last,2,l
        end
        return last,s,l
    else
        # Store the last visited node, to check for a cycle
        last=E1[i]
        # Increment the length of the path
        l=l+1
        # Delete the used edges
        deleteat!(E1,i)
        deleteat!(E2,i)
        # Find the next edge
        j=findfirst(elt->last==elt,E1)
        # If an edge exist recursively call the function with the new edge, otherwise return
        if(!(j isa Nothing))
            last,s,l=ComputePathLength(E1,E2,l,j,1)
        else
            return last,1,l
        end
        return last,s,l
    end
end

# Function to compute the DCJ distance between two genomes
function DCJdistance(G1::Vector{Int}, G2::Vector{Int})
    # Get the Adjacency Graphs for both the genomes
    AG1=AdjacencyGraph(G1)
    AG2=AdjacencyGraph(G2)
    # Initialize the number of cycles and odd length paths to zero
    C=0
    I=0
    N=length(G1)
    # Add the edges of the graphs to the edge vectors
    E1=Vector{Vector{Int}}()
    E2=Vector{Vector{Int}}()
    for i in 1:length(AG1)
        if(AG1[i][1]!=0)
            j=findfirst(elt->AG1[i][1] in elt, AG2)
            push!(E1,AG1[i])
            push!(E2,AG2[j])
        end
        if(AG1[i][2]!=0)
            j=findfirst(elt->AG1[i][2] in elt, AG2)
            push!(E1,AG1[i])
            push!(E2,AG2[j])
        end
    end
    # First compute the non-cyclic paths
    # All non-cyc paths will start at an adjacency that has a telomere
    # First start with the adjacencies of genome 1 that have a telomere
    i=1
    while(i<=length(E1))
        # If the current adjacency is not a telomere skip it
        if(E1[i][1]!=0&&E1[i][2]!=0)
            i=i+1
            continue
        end
        # Store the first node of the path or cycle
        first=E1[i]
        last,s,l=ComputePathLength(E1,E2,0,i,1)
        # Check if there is a cycle present, otherwise check if the length of the path is odd
        if(s==1&&last==first)
            C=C+1
        else
            if(l%2!=0)
                I=I+1
            end
        end
    end
    # Next use the adjacencies of genome 2 that have a telomere
    i=1
    while(i<=length(E2))
        # If the current adjacency is not a telomere skip it
        if(E2[i][1]!=0&&E2[i][2]!=0)
            i=i+1
            continue
        end
        # Store the first node of the path or cycle
        first=E2[i]
        last,s,l=ComputePathLength(E1,E2,0,i,2)
        # Check if there is a cycle present, otherwise check if the length of the path is odd
        if(s==2&&last==first)
            C=C+1
        else
            if(l%2!=0)
                I=I+1
            end
        end
    end
    # Finally use all the remaining adjacencies
    i=1
    while(length(E1)>0)
        # Store the first node of the path or cycle
        first=E1[i] 
        last,s,l=ComputePathLength(E1,E2,0,i,1)
        # Check if there is a cycle present, otherwise check if the length of the path is odd
        if(s==1&&last==first)
            C=C+1
        else
            if(l%2!=0)
                I=I+1
            end
        end
    end
    # Compute the DCJ distance
    return N-(C+I/2)
end