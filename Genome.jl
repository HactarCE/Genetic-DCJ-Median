# Function to convert the Adjacency Graph into the Genome
function Genome(AG::Vector{Vector{Int}})
    G=Vector{Int}()
    while(length(AG)>0)
        # start with the first adjacency
        if AG[1][2]==0
            start=AG[1][2]
        else
            start=AG[1][1]
        end
        # if the first adjacency consists of a telomere the gene is linear
        if(start==0)
            push!(G,start)
        end
        temp=AG[1][2]
        # Delete the adjacency after it has been used
        deleteat!(AG,1)
        # Loop till the end of the gene
        while(start!=-temp)
            push!(G,-temp)
            # find the next adjacency
            i=findfirst(elt->-temp in elt,AG)
            if isnothing(i)
                break
            end
            if(AG[i][2]==-temp)
                temp=AG[i][1]
            else
                temp=AG[i][2]
            end
            deleteat!(AG,i)
        end
        push!(G,start)
    end
    return G
end