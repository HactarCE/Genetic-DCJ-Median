include("AdjacencyGraph.jl")
include("Genome.jl")
# Function to convert the first Genome into the second using DCJ operations
function DCJ(G1::Vector{Int}, G2::Vector{Int}, stop::Int=-1)
    # stop is used for early stopping
    # Generate the Adjacency Graphs of both the Genomes
    AG1=AdjacencyGraph(G1)
    AG2=AdjacencyGraph(G2)
    # If stop is not specified set it to the length of the second Adjacency Graph
    if stop==-1
        stop=length(AG2)
    end
    # If stop is zero return the first genome without making any changes
    if stop==0
        return G1
    end
    # Run the loop from 1 till the stop index
    for i in 1:stop
        # For the adjacencies in the second genome that don't contain a telomere
        if(AG2[i][1]!=0&&AG2[i][2]!=0)
            # Find indices u, v in the first genome that contain the elements in the ith adjacency of the second genome
            u=findfirst(elt->AG2[i][1] in elt,AG1)
            v=findfirst(elt->AG2[i][2] in elt,AG1)
            # If u and v are different, make u equal to the ith adjacency in the second genome by performing the DCJ operation
            if(u!=v)
                p=AG2[i][1]
                q=AG2[i][2]
                if(AG1[u][1]!=p)
                    l=AG1[u][1]
                else
                    l=AG1[u][2]
                end
                if(AG1[v][1]!=q)
                    m=AG1[v][1]
                else
                    m=AG1[v][2]
                end
                AG1[u][1]=p
                AG1[u][2]=q
                AG1[v][1]=l
                AG1[v][2]=m
            end
        # For all the adjacencies in the second genome that contain a telomere
        else
            if(AG2[i][1]!=0)
                p=AG2[i][1]
            else
                p=AG2[i][2]
            end
            # Find the vertex u in the first genome that contains the same element
            u=findfirst(elt->p in elt,AG1)
            if(AG1[u][1]!=p)
                l=AG1[u][1]
            else
                l=AG1[u][2]
            end
            # If vertex u is not equal to vertex i
            if(l!=0)
                # Perfom the DCJ operation on vertex u
                AG1[u][1]=p
                AG1[u][2]=0
                push!(AG1,[l,0])
            end
        end
    end
    # Return the genome represented by the resulting adjacency graph
    return Genome(AG1)
end