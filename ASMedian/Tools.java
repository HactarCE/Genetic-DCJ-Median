

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

 // the class to represent the 0-edges constructed upon on discovery of adequate subgraphs
 class EF {
		ArrayList<ArrayList<Short>> black;
		String info;
		public EF(){
			black=new ArrayList<ArrayList<Short>>();
		}
		public EF(EF ef){
			black=new ArrayList<ArrayList<Short>>();
			for(ArrayList<Short> m:ef.black) black.add(new ArrayList<Short>(m));
			info=ef.info;
		}
	}
 						
 // the class to represent the result for the median problem
 class Solution {
	 
     	int gene_number;
		int cycle_number;
		int distance;
		int heuristic2,maxh;
		int upper_bound;
		int lower_bound;
		//long size;
		long time;
		long h_time2;
		boolean finished;
		int circular;
		short[] median;
		String median_genome;
		int[] cycle;
		
		public Solution() {
			finished=true;
			cycle=new int[3];
			maxh=0;
		}
		
		public Solution(Solution sl) {
		    	gene_number=sl.gene_number;
			distance=sl.distance;
			cycle_number=sl.cycle_number;
			maxh=sl.maxh;
			heuristic2=sl.heuristic2;
			upper_bound=sl.upper_bound;
			lower_bound=sl.lower_bound;
			time=sl.time;
			h_time2=sl.h_time2;
			finished=sl.finished;
			if(median!=null) {
				median=new short[sl.median.length];
				for(int i=0;i<median.length;i++) median[i]=sl.median[i];
			}
		}
		
		public void get_median_genome(){
			final int NULL=-1;
			StringBuffer genome =new StringBuffer();
			genome.append(">the median genome\n");
			int adjacency[]=new int[2*gene_number];
			for(int i=0;i<2*gene_number;i+=2) {
				int left=median[i], right=median[i+1];
				adjacency[left]=right;
				adjacency[right]=left;
			}
			boolean keep_finding=true;
			int start=0, left=NULL, right=NULL,gene=NULL;
			circular=0;
			wh:while(keep_finding) {
				circular++;
				genome.append("C"+circular+": ");
				left=start;
				do{
					if(left%2==0) {
						right=left+1;
						gene=left/2+1;
					}
					else {
						right=left-1;
						gene=-(left/2+1);
					}
					genome.append(gene+" ");
					left=adjacency[right];
					adjacency[left]=adjacency[right]=NULL;
				}while(left!=start);
				genome.append("\n");
				for(int i=0;i<2*gene_number;i++) {
					if(adjacency[i]!=NULL) {start=i; continue wh;} 
				}
				keep_finding=false;
			}
			median_genome=genome.toString();
		}
		
		public String toString() {
			int diff2=cycle_number-heuristic2;
			float dp2=(float)(diff2*100.0/cycle_number);
			float ddp2=(float)(diff2*100.0/distance);
			StringWriter sw=new StringWriter();
			PrintWriter stringout=new PrintWriter(sw);
			if(finished) 
				stringout.print(">Solved\n");
			else
				stringout.print(">Unsolved\n");
			stringout.printf("Maximum number of cycles:\t%6d\t\tMinimum DCJ distance:\t%6d\nInitial upper bound:\t%6d\t\t\tInitial lower bound:\t%6d\ncycle number got from the heuristic method:\t%6d\nnumber of circular chromosomes:\t%6d\n", cycle_number, (3*gene_number-cycle_number), upper_bound, lower_bound, heuristic2,circular);
			stringout.printf("Time (in millisecond):\t%14d\t\tHeuristic time (in millisecond):%14d\n",time,h_time2);
			return sw.toString();
		}
	}
 
 // the class to represent the parameter information used to generate/reuse simulated data
 class Parameter {
		int repeat;
		int gene_number;
		int reversal;
		int e1,e2,e3;
		boolean sym;
		
		public Parameter(String s){
			Scanner sc=new Scanner(s);
			gene_number=sc.nextInt();
			int in[]=new int[4];
			int cnt=0;
			while(sc.hasNextInt()) {
				in[cnt]=sc.nextInt();
				cnt++;
			}
			if(cnt==2) {reversal=in[0];repeat=in[1];sym=true;}
			else if(cnt==4) {e1=in[0];e2=in[1];e3=in[2];repeat=in[3];sym=false;}
			else {System.out.println("error in specifying input parameters!");System.exit(-1);}
		}
		
		public Parameter(int p[]) {
			if(p.length!=3 || p.length!=5) {
				System.out.println("error in specifying input parameters!");System.exit(-1);
			}
			gene_number=p[0];
			if(p.length==3) {
				reversal=p[1];
				repeat=p[2];
			}
			else {
				e1=p[1];
				e1=p[2];
				e1=p[3];
				repeat=p[4];
			}
		}
	}
 
 
 // the class to represent the chromosome information
 class Chromosome {
		public int genes[];
		public int adjacency[];
		public int size;
		
		public Chromosome(){}
		
		public Chromosome(int n) {
			size=n;
			genes=new int[size];
			for(int i=0;i<size;i++) genes[i]=i+1;
			get_adjacency();
		}
		public Chromosome(Chromosome c) {
			size=c.size;
			genes=new int[size];
			adjacency=new int[2*size];
			for(int i=0;i<size;i++) genes[i]=c.genes[i];
			for(int i=0;i<2*size;i++) adjacency[i]=c.adjacency[i];
		}
		public Chromosome(int a[]) {
			size=a.length;
			genes=new int[size];
			for(int i=0;i<size;i++) genes[i]=a[i];
			get_adjacency();
		}
		public void get_adjacency(){
			int[] unsigned=new int[2*size];
			if(genes[0]>0) {unsigned[0]=2*genes[0]-1;unsigned[2*size-1]=2*genes[0]-2;}
			else {unsigned[0]=-2*genes[0]-2;unsigned[2*size-1]=-2*genes[0]-1;}
			
			for(int i=1;i<size;i++) {
				if(genes[i]>0) {unsigned[2*i-1]=2*genes[i]-2;unsigned[2*i]=2*genes[i]-1;}
				else {unsigned[2*i-1]=-2*genes[i]-1;unsigned[2*i]=-2*genes[i]-2;}
			}
			
			adjacency=new int[2*size];
			for(int i=0;i<size;i++) {
				int left=unsigned[2*i], right=unsigned[2*i+1];
				adjacency[left]=right;adjacency[right]=left;
			}
		}
		public void reverse(int r) {
//			System.out.println(r);
			Random rd=new Random();
			for(int i=0;i<r;i++) {
				int left=rd.nextInt(size)+1;
				int right=rd.nextInt(size)+1;
				if(left==right) continue;
				if(left>right) {int tmp=left;left=right;right=tmp;}
				int temp[]=new int[right-left];
				for(int j=0;j<right-left;j++) temp[j]=-genes[right-j-1];
				for(int j=0;j<right-left;j++) genes[left+j]=temp[j];
			}	
		}
		
		static class Convert {
			public static String Graph_2_Genome (Graph g) {
				final int NULL=-1;
				StringBuffer genomes=new StringBuffer();
				for(int gcount=0;gcount<3;gcount++) {
					int adjacency[]=new int[g.vertice_size];
					for(int i=0;i<g.vertice_size;i++) adjacency[i]=g.neighbor[i][gcount];
					boolean keep_finding=true;
					int start=0, left=NULL, right=NULL,gene=NULL;
					genomes.append(">Genome"+(gcount+1)+"\n");
					wh:while(keep_finding) {
						genomes.append("C: ");
						left=start;
						do{
							if(left%2==0) {
								right=left+1;
								gene=left/2+1;
							}
							else {
								right=left-1;
								gene=-(left/2+1);
							}
							genomes.append(gene+" ");
							left=adjacency[right];
							adjacency[left]=adjacency[right]=NULL;
						}while(left!=start);
						genomes.append("\n");
						for(int i=0;i<g.vertice_size;i++) {
							if(adjacency[i]!=NULL) {start=i; continue wh;} 
						}
						keep_finding=false;
					}
				}
				return genomes.toString();
			}
		}
	}
 
 	
 	



