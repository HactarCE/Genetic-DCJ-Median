

import java.util.ArrayList;
import java.util.*;
import java.io.*;

public class IOCircular implements Serializable{
	public short neighbor[][]; // the adjacency matrix representing MBGs only 
	int vertice_size; // number of remaining vertices 
	int edge_size; // the size of the remaining graph 
	int gene_size; // number of edges/genes

	public IOCircular(Graph g) {
		// copy constructor
		gene_size=g.gene_size;
		edge_size = g.edge_size;
		vertice_size = g.vertice_size;

		// copy the neighbor array and the cap array
		neighbor = new short[vertice_size][3];
		for (int i = 0; i < vertice_size; i++)
			for (int j = 0; j < 3; j++)
				neighbor[i][j] = g.neighbor[i][j];
	}

	public IOCircular(Chromosome genome[]) {
		if (genome.length != 3)
			{
			System.out.println("Only 3 genomes are needed!"); 
			System.exit(-1); // when the input data is not consistent}
			}

		/////////////////////////////////////////////////////////////
		edge_size=genome[0].size;
		vertice_size = 2* edge_size;  // number of vertices
		gene_size = edge_size;
		neighbor = new short[vertice_size][3];
		///////////////////// copy adjacency  ////////////////////////////
		for (int color = 0; color < 3; color++) { // for each color
			//------------  construct the adjacency array ------------//
			genome[color].get_adjacency();
			for(int i=0;i<vertice_size;i++) 
				neighbor[i][color]=(short) genome[color].adjacency[i];
		} // done for all three genomes
	}

}
