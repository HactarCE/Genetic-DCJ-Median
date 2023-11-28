# Function to convert the Adjacency Graph into the Genome
function Genome(AG::Vector{Vector{Int}})
    # AG is the Adjacency Graph
    G=Vector{Vector{Int}}()
    while(length(AG)>0)
        chrom=Vector{Int}()
        # start with the first adjacency
        idx=findfirst(elt->0 in elt,AG)

        if isnothing(idx)
            idx=1
        end
        if AG[idx][2]==0
            start=AG[idx][2]
            temp=AG[idx][1]
        else
            start=AG[idx][1]
            temp=AG[idx][2]
        end
        # if the first adjacency consists of a telomere the gene is linear
        if(start==0)
            push!(chrom,start)
        end
        # Delete the adjacency after it has been used
        deleteat!(AG,idx)
        # Loop till the end of the gene
        while(start!=-temp)
            push!(chrom,-temp)
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
        push!(chrom,start)
        push!(G,chrom)
    end
    return G
end