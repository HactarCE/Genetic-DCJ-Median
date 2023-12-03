using CSV, DataFrames
using Gtk, Graphics

SPECIES_PERMUTATIONS = permutations

# Height of idiogram display as a portion of the whole rainbow display for a chromosome
IDIOGRAM_HEIGHT = 0.3

# Padding between chromosomes, in pixels
CHROMOSOME_PADDING = 5

println("Loading colors ...")
include("ColorMap.jl")
println("Loading permutations ...")
include("DrosophilaPermutations.jl")

# CSV_PATH = open_dialog_native("Select CSV File", GtkNullContainer(), ["*.csv"])

# Draws a rectangle with color `color` at size `(x, y)` with size `(w, h)`.
function draw_colored_rect!(c :: GtkCanvas, color :: Tuple{Float64, Float64, Float64}, x, y, w, h)
    ctx = getgc(c)

    r, g, b = color

    # Round X positions
    x1 = round(x)
    x2 = round(x + w)

    rectangle(ctx, x1, y, x2-x1, h)
    set_source_rgb(ctx, r, g, b)
    fill(ctx)
end

# Draws a rainbow plot representing the signed permutation `seq` relative to
# `gene_origins`, which maps gene number to a tuple `(chromosome_index,
# index_within_chromosome, chromosome_length)`.
function draw_permutation_rainbow!(c :: GtkCanvas,
                                   gene_origins :: Vector{Tuple{Int, Int, Int}},
                                   seq :: Vector{Int},
                                   x, y, w, h)
    m = length(seq)
    each_width = w/m
    for i in 1:m
        chromosome_index, gene_index, chromosome_length = gene_origins[abs(seq[i])]
        t = (gene_index - 1) / (chromosome_length - 1)
        draw_colored_rect!(c, get_rainbow_color(t), x, y, each_width, h)
        x += each_width
    end
end

# Draws an idiogram representing the chromosomal origins of the signed
# permutation `seq` relative to `gene_origins`, which maps gene number to a
# tuple `(chromosome_index, index_within_chromosome, chromosome_length)`.
function draw_idiogram!(c :: GtkCanvas,
                        gene_origins :: Vector{Tuple{Int, Int, Int}},
                        seq :: Vector{Int},
                        x, y, w, h)
    # Find blocks of genes that came from the same chromosome. Build a list
    # where for each block we store where it came from (`origin`) and a length
    # (number of genes).
    blocks = []
    for origin in map(g -> gene_origins[abs(g)][1], seq)
        if isempty(blocks) || blocks[end][1] != origin
            push!(blocks, [origin, 1])
        else
            blocks[end][2] += 1
        end
    end

    m = length(seq)
    for (chromosome_index, len) in blocks
        dx = len * w/m
        color = get_discrete_color(chromosome_index)
        draw_colored_rect!(c, color, x, y, dx, h)
        x += dx
    end
end

# Draws the rainbow plot and idiogram for a signed permutation `seq` relative to
# `gene_origins`, which maps gene number to a tuple `(chromosome_index,
# index_within_chromosome, chromosome_length)`.
function draw_chromosome!(c :: GtkCanvas,
                          gene_origins :: Vector{Tuple{Int, Int, Int}},
                          seq :: Vector{Int},
                          x, y, w, h)
    h2 = h * IDIOGRAM_HEIGHT
    h1 = h - h2
    draw_permutation_rainbow!(c, gene_origins, seq, x, y, w, h1)
    draw_idiogram!(c, gene_origins, seq, x, y + h1, w, h2)
end

# Draws the rainbow plot and idiogram for every chromosome in the `genome`
# relative to `gene_origins`. The genome is represented as a list of
# chromosomes, and each chromosome is a signed list of genes. Every gene must
# have a unique number, although the particular value doesn't matter.
function draw_genome!(c :: GtkCanvas,
                      gene_origins :: Vector{Tuple{Int, Int, Int}},
                      genome :: Vector{Vector{Int}},
                      x, y, w, h)
    # Compute the height for each chromosome display (including padding)
    dy = (h + CHROMOSOME_PADDING) / length(genome)
    # Remove padding
    h1 = dy - CHROMOSOME_PADDING

    # Draw each chromosome.
    for chromosome in genome
        draw_chromosome!(c, gene_origins, chromosome, x, y, w, h1)
        y += dy
    end
end

# Computes the `gene_origins` vector required by drawing functions from a
# candidate median genome. The genome is represented as a list of chromosomes,
# and each chromosome is a signed list of genes. Every gene must have a unique
# number, although the particular value doesn't matter.
function compute_gene_origins_vector(median :: Vector{Vector{Int}}) :: Vector{Tuple{Int, Int, Int}}
    # Make a dictionary keyed by gene where the value is the position of the
    # gene in the median genome, represented as a tuple `(chromosome_index,
    # gene_index, chromosome_length)`
    gene_origins = [(0, 0, 0) for _ in 1:maximum(map(chromosome -> maximum(abs.(chromosome)), median))]
    for (chromosome_index, chromosome) in enumerate(median)
        for (i, gene) in enumerate(chromosome)
            if gene_origins[abs(gene)] != (0, 0, 0)
                println("Duplicate gene! Bad input data")
            end
            gene_origins[abs(gene)] = (chromosome_index, i, length(chromosome))
        end
    end
    gene_origins
end

# Shows the GUI to select three species, then shows the GUI to run the genetic
# algorithm on those three species.
function gui_select_species!(win)
    vbox = GtkBox(:v, margin=15)
    l = GtkLabel("Select 3 species to compare", margin=10)
    b = GtkButton("Medianize!", margin=15, sensitive=false)

    # Make a checkbox for each species.
    push!(vbox, l)
    selected_species = Set()
    for species in keys(SPECIES_PERMUTATIONS)
        c = GtkCheckButton(species)
        push!(vbox, c)

        # Enable or disable the button depending on how many species are
        # selected.
        signal_connect(c, "toggled") do widget
            if get_gtk_property(widget, :active, Bool)
                push!(selected_species, species)
            else
                delete!(selected_species, species)
            end
            set_gtk_property!(b, :sensitive, length(selected_species) == 3)
        end
    end
    push!(vbox, b)
    push!(win, vbox)
    showall(win)

    signal_connect(b, :clicked) do widget
        push!(GtkNullContainer(), vbox)
        gui_comparison!(win, (pop!(selected_species),
                              pop!(selected_species),
                              pop!(selected_species)))
    end
end

# Shows the GUI to run the genetic algorithm on the three species `a, b, c`.
function gui_comparison!(win, species_names :: Tuple{String, String, String})
    vbox = GtkBox(:v, margin=15)

    push!(vbox, GtkLabel("Genetic DJC Median Algorithm", margin=10))

    step_button = GtkButton("Step")
    push!(vbox, step_button)

    hbox = GtkBox(:h, margin=15)
    push!(vbox, hbox)

    # Make a label and canvas for each species
    species_canvases = []
    for species in species_names
        species_vbox = GtkBox(:v, margin=5)
        push!(hbox, species_vbox)

        push!(species_vbox, GtkLabel(species))
        c = GtkCanvas()
        set_gtk_property!(c, :width_request, 100)
        set_gtk_property!(c, :height_request, 200)
        set_gtk_property!(c, :hexpand, true)
        set_gtk_property!(c, :vexpand, true)
        push!(species_canvases, c)
        push!(species_vbox, c)
    end

    # Preprocess median
    species_permutations :: Vector{Vector{Vector{Int}}} = [SPECIES_PERMUTATIONS[species] for species in species_names]
    gene_origins :: Vector{Tuple{Int, Int, Int}} = []
    function update_genomes(median)
        empty!(gene_origins)
        append!(gene_origins, compute_gene_origins_vector(median))
    end
    update_genomes(species_permutations[1])

    # Update drawing
    for (species, permutations, c) in zip(species_names, species_permutations, species_canvases)
        @guarded draw(c) do widget
            draw_genome!(c, gene_origins, permutations, 0, 0, width(c), height(c))
        end
    end

    push!(win, vbox)
    showall(win)
end

win = GtkWindow(title="Genetic DCJ Median")

gui_select_species!(win)

if !isinteractive()
    @async Gtk.gtk_main()
    Gtk.waitforsignal(win, :destroy)
end
