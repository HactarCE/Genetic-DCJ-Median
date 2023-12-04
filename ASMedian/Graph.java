

import java.util.ArrayList;
import java.io.*;
import java.util.*;

public class Graph implements Serializable {
	final static short NULL = -1; // unassigned
	public short neighbor[][]; // the adjacency matrix representing MBGs only 
	public short median[]; // the array to store the median adjacency
	public short lower_median[]; // the array to store the median adjacency
	public short label[]; // the array to store the mapping function between the vertices indices and the gene indices
	int vertice_size; // number of remaining vertices 
	int edge_size; // the size of the remaining graph 
	int gene_size; // number of edges/genes; for a given instance this variable remains constant
	int cycle_number;
	int cycle[];
	int lower_cycle[];
	int upper_bound;  
	int lower_bound;
	int as22,as0,as1,as2,as4;

	private Graph() { // no private constructor
	}	

	public Graph(Graph g) {
		// copy constructor
		gene_size=g.gene_size;
		edge_size = g.edge_size;
		vertice_size = g.vertice_size;

		// copy the neighbor array and the cap array
		neighbor = new short[vertice_size][3];
		for (int i = 0; i < vertice_size; i++)
			for (int j = 0; j < 3; j++)
				neighbor[i][j] = g.neighbor[i][j];
		
		median = new short[2*gene_size]; for(int i=0;i<2*gene_size;i++) median[i]=g.median[i];
		lower_median = new short[2*gene_size]; for(int i=0;i<2*gene_size;i++) lower_median[i]=g.lower_median[i];
		
		label = new short[2*edge_size]; for(int i=0;i<2*edge_size;i++) label[i]=g.label[i];
		
		// copy the other statistic variables
		cycle_number = g.cycle_number;
		upper_bound = g.upper_bound;
		lower_bound = g.lower_bound;
		cycle=new int[3];
		for(int i=0;i<3;i++) cycle[i]=g.cycle[i];
		lower_cycle=new int[3];
		for(int i=0;i<3;i++) lower_cycle[i]=g.lower_cycle[i];
		
		as22=g.as22;as0=g.as0;as1=g.as1;as2=g.as2;as4=g.as4;
	}

	public Graph(IOCircular g) {
		// copy constructor
		gene_size=g.gene_size;
		edge_size = g.edge_size;
		vertice_size = g.vertice_size;

		// copy the neighbor array and the cap array
		neighbor = new short[vertice_size][3];
		for (int i = 0; i < vertice_size; i++)
			for (int j = 0; j < 3; j++)
				neighbor[i][j] = g.neighbor[i][j];
		
		median = new short[2*gene_size]; for(int i=0;i<2*gene_size;i++) median[i]=NULL;
		lower_median = new short[2*gene_size]; for(int i=0;i<2*gene_size;i++) lower_median[i]=NULL;
		label = new short[2*gene_size]; for(short i=0;i<2*gene_size;i++) label[i]=i;

		// copy the other statistic variables
		cycle_number = 0;
		cycle=new int[3];
		for(int i=0;i<3;i++) cycle[i]=0;
		lower_cycle=new int[3];
		for(int i=0;i<3;i++) lower_cycle[i]=0;
		get_bounds();
		as22=0;as0=0;as1=0;as2=0;as4=0;
	}
	
	public void shrink(ArrayList<Short> black) {
		short map[]=new short[vertice_size];
		boolean valid[]=new boolean[vertice_size];
		for(int i=0;i<vertice_size;i++) valid[i]=true;
		for(short b:black) valid[b]=false;
		short cnt=-1;
		for(int i=0;i<vertice_size;i++) {
			if(valid[i]) {
				cnt++;
				map[i]=cnt;
			}
			else map[i]=NULL;
		}
		
		// the real shrinking part
		int black_size=black.size()/2;
		short left, right;
		for(int i=0;i<black_size;i++) {
			left=black.get(2*i);
			right=black.get(2*i+1);
			for(int color=0;color<3;color++) {
				if(neighbor[left][color]==right) {
				    cycle_number++;
				    cycle[color]++;
				}
				else {
					neighbor[neighbor[left][color]][color]=neighbor[right][color];
					neighbor[neighbor[right][color]][color]=neighbor[left][color];
				}
			}
		}
		
		int v=vertice_size-black.size();
		int s=edge_size-black_size;
		short old_neighbor[][]=neighbor;
		neighbor=new short[v][3];
		short old_label[]=label;
		label=new short[v];
	
		for(int i=0;i<black.size();i++) {
			median[i+2*(gene_size-edge_size)]=old_label[black.get(i)];
		}
		
		for(short i=0;i<vertice_size;i++) {
			if(!valid[i]) continue;
			for(int color=0;color<3;color++) {
				neighbor[map[i]][color]=map[old_neighbor[i][color]];
			}
			label[map[i]]=old_label[i];
		}
		vertice_size=v;
		edge_size=s;
		get_bounds();
	}

	public int connected_by(short l, short r) {
		if (l >=vertice_size || r >= vertice_size )
			return NULL;
		for (int color = 0; color < 3; color++) {
			if (neighbor[l][color] == r)
				return color;
		}
		return NULL;
	}

	public  boolean is_connected(short l, short r) {
		if(connected_by(l,r)==NULL) return false;
		else return true;
	}

	public  short two_connected_by(short l, short r) {
		if (l >=vertice_size || r >= vertice_size )
			return NULL;
		if (l == r)
			return NULL;
		for (int color = 0; color < 3; color++) {
			short lc = neighbor[l][color];
			if (lc >= vertice_size)
				continue;
			for (int i = 1; i <= 2; i++) {
				int c = (color + i) % 3;
				if (lc == neighbor[r][c])
					return lc;
			}
		}
		return NULL;
	}

	public void get_bounds() {
	    	int c[]=new int[3];
		int lower_ind=-1;
		c[2]=count_cycle(0, 1);
		c[1]=count_cycle(0, 2);
		c[0]=count_cycle(1, 2);
		if(c[0]<=c[1]&& c[0]<=c[2]) lower_ind=0;
		else if(c[1]<=c[2]&& c[1]<=c[2]) lower_ind=1;
		else if(c[2]<=c[0]&& c[2]<=c[1]) lower_ind=2;
		upper_bound = cycle_number+ (3 * edge_size + c[0]+c[1]+c[2])/2;
		lower_bound=cycle_number+edge_size+c[0]+c[1]+c[2]-c[lower_ind];
		
		// lower_cycle
		for(int i=0;i<3;i++) {
		    if(i==lower_ind) lower_cycle[i]=cycle[i]+edge_size;
		    else lower_cycle[i]=cycle[i]+c[i];
		}

		// lower_median: the median when the lower bound is taken
		for(int i=0;i<2*gene_size;i++) lower_median[i]=median[i];
		boolean valid[]=new boolean[vertice_size];
		for(int i=0;i<vertice_size;i++) valid[i]=true;
		int k=-1;
		for(int i=0;i<vertice_size;i++) {
		    if(!valid[i]) continue;
		    lower_median[2*(gene_size-edge_size)+(++k)]=label[i];
		    lower_median[2*(gene_size-edge_size)+(++k)]=label[neighbor[i][lower_ind]];
		    valid[i]=valid[neighbor[i][lower_ind]]=false;
		}
	}

	public int count_cycle(int c1, int c2 ) {
		int cycles=0;
		boolean unused[]=new boolean[vertice_size];
		for(short i=0;i<vertice_size;i++) unused[i]=true;
		short start, left,right;
		for(short i=0;i<vertice_size;i++) {
			if(!unused[i]) continue;
			start=left=i;
			do {
				right=neighbor[left][c1];
				unused[left]=unused[right]=false;
				left=neighbor[right][c2];
			}while(left!=start);
			cycles++;
		}
		return cycles;
	}
}


	
