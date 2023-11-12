using DelimitedFiles
# Function to read the input file
function Input(filename::String)
    file=open(filename,"r")
    G=readdlm(file)
    close(file)
    return G[1,:],G[2,:],G[3,:]
end