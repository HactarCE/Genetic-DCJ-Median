include("AdjacencyGraph.jl")
include("Genome.jl")
# Function to convert the first Genome into the second using DCJ operations
function DCJ(G1::Vector{Vector{Int}}, G2::Vector{Vector{Int}}, num_DCJ::Int, count_operations::Bool=false)
    # G1 is the first Genome
    # G2 is the second Genome
    # num_DCJ is the number of DCJ operations to perform
    # if count_operations is true return the number of DCJ operations performed

    # Generate the Adjacency Graphs of both the Genomes
    AG1=AdjacencyGraph(G1)
    AG2=AdjacencyGraph(G2)
    # If num_DCJ is zero return the first genome without making any changes
    if num_DCJ==0
        if count_operations==true
          return 0,G1
        else
          return G1
        end
    end
    count=0
    # Run the loop from 1 till the stop index
    # First check adjacencies that do not contain telomeres
    for i in 1:length(AG2)
        if count>=num_DCJ
          break
        end
        # For the adjacencies in the second genome that don't contain a telomere
        if(AG2[i][1]!=0&&AG2[i][2]!=0)
            # Find indices u, v in the first genome that contain the elements in the ith adjacency of the second genome
            u=findfirst(elt->AG2[i][1] in elt,AG1)
            v=findfirst(elt->AG2[i][2] in elt,AG1)
            # If u and v are different, make u equal to the ith adjacency in the second genome by performing the DCJ operation
            if(u!=v)
                count+=1
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
                if(p==0&&q==0)
                    deleteat!(AG1,u)
                end
                if(l==0&&m==0)
                    deleteat!(AG1,v)
                end
            end
        end
    end
    # Next check adjacencies that contain telomeres
    for i in 1:length(AG2)
      if count>=num_DCJ
        break
      end
      if(AG2[i][1]==0||AG2[i][2]==0)
        # For all the adjacencies in the second genome that contain a telomere
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
          count+=1
          # Perfom the DCJ operation on vertex u
          AG1[u][1]=p
          AG1[u][2]=0
          push!(AG1,[l,0])
        end
      end
    end
    # Return the genome represented by the resulting adjacency graph
    if count_operations==true
      return count,Genome(AG1)
    else
      return Genome(AG1)
    end
end