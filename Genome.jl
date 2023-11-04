# Function to convert the Adjacency Graph into the Genome
function Genome(AG::Vector{Vector{Int}})
    G=Vector{Int}()
    # The Genome starts with a 0
    # Find the first occurence of a 0 in the Adjacency Graph
    i=findfirst(elt->0 in elt,AG)
    if(AG[i][1]==0)
        temp=AG[i][2]
    else
        temp=AG[i][1]
    end
    # Join the gene to its negative to find the next gene in the Genome till the telomere is found
    while(temp!=0)
        push!(G,-temp)
        i=findfirst(elt->-temp in elt,AG)
        if(AG[i][2]==-temp)
            temp=AG[i][1]
        else
            temp=AG[i][2]
        end
    end
    return G
end