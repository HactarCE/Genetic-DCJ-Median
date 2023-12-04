

import java.util.ArrayList;
import java.util.ArrayList;
import java.lang.Math;
import java.io.*;

public class ASMedian {

	/**
	 * @param args
	 */
	static int BLOCK = 2000;
	static long TIME = 600000;

	public static Solution solver(Graph g) throws Exception {
		// *****************************************************//
		// ************            Initialization         ******************//
		// *****************************************************//

		long start_time = System.currentTimeMillis();

		/***************** info          *****************************/
		Solution info = new Solution();
		info.gene_number=g.gene_size;
		info.upper_bound = g.upper_bound;
		info.lower_bound = g.lower_bound;
		info.heuristic2 = heuristic2(g,info);
		info.h_time2 = System.currentTimeMillis() - start_time;
		start_time=System.currentTimeMillis();

		/******************* setting the maximum upper bound ************************/
		int top = g.upper_bound;
		int maxup=top;
		int number = info.maxh;
		long total = 0, search_count = 0;
		String name;
		File file;
		ObjectInputStream oin;
		ObjectOutputStream oout;

		int ub, lb; // local bounds

		/********************************************************/
		long checking[] = new long[top + 1]; // checking is the array to count the number of graph for each upper bound
		for (int i = 0; i <= top; i++)  checking[i] = 0;

		/********************************************************/
		int file_checking[] = new int[top + 1]; // file_checking: counts the number of files storing graphs for each upper bound
		for (int i = 0; i <= top; i++)  file_checking[i] = 0;

		/********************************************************/
		ArrayList<ArrayList<Graph>> search = new ArrayList<ArrayList<Graph>>(top + 1); // search: the graphs in the memory
		for (int i = 0; i <= top; i++) search.add(new ArrayList<Graph>());


		/*******************adding current_graph to the next stage search ************************/
		Graph iMBG = new Graph(g);
		search.get(iMBG.upper_bound).add(iMBG);
		total++; // total number of graphs remained
		checking[iMBG.upper_bound]++;

		/******************* PRINTING ************************/
		System.out.println("Initial Lower bound:" + info.lower_bound + "\tInitial upper bound:" + info.upper_bound + "\tHeuristic :" + info.maxh + "\ntop stack:" + search.get(maxup).size()
				+ "\toptimistic graphs:" + checking[maxup] + "\ttotal:" + total + "\n");


		loop: while (total > 0 && maxup>number) {
			while (checking[maxup] == 0) {
				// if there is no graph whose upper bound is maxup
				maxup--;
				System.out.println("maxup decreases by one");
				System.out.println("L:" + number + "\tU:" + maxup + "\ttop stack:" + search.get(maxup).size()
						+ "\toptimistic graphs:" + checking[maxup] + "\ttotal:" + total);
				if (maxup<= number) { 
					// number is the optimal number (lower bounds are used) of cycles achieved so far
					break loop;
					// EXIT 1
				} 
			}

			/******************* to load graphs from harddisk ************************/
			if (search.get(maxup).size() == 0) {
				name = "TMPR/tmp" + maxup + "_" + file_checking[maxup];
				file = new File(name);
				oin = new ObjectInputStream(new BufferedInputStream( new FileInputStream(file)));

				int cc = 0; // DELETE
				try {
					while (true) {
						search.get(maxup).add((Graph) oin.readObject());
						cc++;
					}
				} catch (IOException ioe) {  // a bad design to use IOException to exit the while loop
					;
				}

				System.out.println("copied from the file " + cc);

				oin.close();
				file.delete();
				file_checking[maxup]--;
			}

			/******************* take one graph from the search list  ************************/
			iMBG = search.get(maxup).remove( search.get(maxup).size() - 1);
			checking[maxup]--;
			total--;
			search_count++;
			//	    System.out.println(search_count);

			//	    System.out.println("size:"+iMBG.edge_size+"\tL:" + number + "\tU:" + maxup + "\ttop stack:" + search.get(maxup).size()
			//				+ "\toptimistic graphs:" + checking[maxup] + "\ttotal:" + total + "\t in loop 2 pre");

			/******************* to detect adequate subgraphs ************************/
			EF result = new EF();
			EF two = new EF();
			if (Adequate.AS1(iMBG, result)) ;
			else if (Adequate.AS2(iMBG, result)) {
				if (result.black.size() > 1) {
					two = result;
					result = new EF();
					if (Adequate.AS4(iMBG, result)) ;
					else result = two;
				} 
				else ;
			} 
			else if (Adequate.AS4(iMBG, result)) ;
			else Adequate.AS0(iMBG, result);
			
			if(result.black.size()==2) iMBG.as22++;
			else if(result.black.size()>2) iMBG.as0++;
			else if(result.black.size()==1) {
				if(result.info=="AS1") iMBG.as1+=(result.black.get(0).size()/2);
				else if(result.info=="AS4") iMBG.as4+=(result.black.get(0).size()/2);
				else iMBG.as2+=(result.black.get(0).size()/2);
			}

			//	    System.out.print(result.info+"\t");
			/************ construct sub iMBG's *************/
			sub:for(ArrayList<Short> black: result.black) {
				Graph tmp_MBG=new Graph(iMBG);
				tmp_MBG.shrink(black);

				/******************* to check each possible iMBG graphs upon obtaining the list of moves from "result" ************************/
				if (tmp_MBG.edge_size == iMBG.edge_size)  System.exit(1); // just to detect the error that no 0-edges are added
				if (tmp_MBG.lower_bound > iMBG.upper_bound) {System.out.println("The child's lower bound is larger than parent's upper bound\nold_upper"+iMBG.upper_bound);System.exit(1);}
				if (tmp_MBG.upper_bound > iMBG.upper_bound) {System.out.println("The child's upper bound is larger than parent's upper bound");System.exit(1);}

				/******************* to get the bounds for the child graph ************************/
				ub = tmp_MBG.upper_bound;
				lb = tmp_MBG.lower_bound; // use lower bound instead of real cycle number

				/******************* when the upper bound is no larger than the maximum number of cycles found so far ************************/
				if (ub <= number) continue sub; // KEY: this is the place to change if we want to find all possible solutions

				/************ another impossible error where the up bound is larger than the maximum of upper bounds ******************/
				if (ub > maxup) {System.out.println("got you");System.exit(-1);}

				/******************* when lower bound meets the maximum upper bound---end of the story ************************/
				if (lb >= maxup) { 
					//the tmp files need to be deleted
					for (int i = number + 1; i <=maxup; i++) {
						for (int j = 1; j <= file_checking[i]; j++) {
							name = "TMPR/tmp" + i + "_" + j;
							file = new File(name);
							file.delete();
						}
					}
					number = lb;
					info.median=new short[2*tmp_MBG.gene_size];
					for(int ii=0;ii<2*tmp_MBG.gene_size;ii++) info.median[ii]=tmp_MBG.lower_median[ii];
					for(int ii=0;ii<3;ii++) info.cycle[ii]=tmp_MBG.lower_cycle[ii];

					break loop;
				} 
				/******************* update cycle number ************************/
				else if (lb > number) { // find a better cycle number
					for (int i = number + 1; i <= lb; i++) {
						for (int j = 1; j <= file_checking[i]; j++) {
							name = "TMPR/tmp" + i + "_" + j;
							file = new File(name);
							file.delete();
						}
						file_checking[i] = 0;
						search.get(i).clear();
						total -= checking[i];
						checking[i] = 0;

						System.out.println("Lower bound increases from "+number+" to "+lb+"!");
						number = lb;  // update the cycle number
						info.median=new short[2*tmp_MBG.gene_size];
						for(int ii=0;ii<2*tmp_MBG.gene_size;ii++) info.median[ii]=tmp_MBG.lower_median[ii];
						for(int ii=0;ii<3;ii++) info.cycle[ii]=tmp_MBG.lower_cycle[ii];
					}
				}

				/********** when the size of the graph is not 0 ******************/
				if (tmp_MBG.edge_size > 0) {
					total++;
					checking[ub]++;
					search.get(ub).add(tmp_MBG);

					/********** if the search array contains too many graphs, store them on harddisk instead******************/
					if (search.get(ub).size() >= BLOCK && ub != maxup) {
						file_checking[ub]++;
						name = "TMPR/tmp" + ub + "_" + file_checking[ub];
						file = new File(name);
						oout = new ObjectOutputStream(new BufferedOutputStream( new FileOutputStream(file)));
						for (Graph gt : search.get(ub)) oout.writeObject(gt);
						oout.close();
						search.get(ub).clear();
					}
				}
			} // black

			/********** to check whether the running time exceeds the maximum allowed time*****************/
			/********** if TIME is set to negative number, it means there is no time limit ******************/
			if (search_count >= BLOCK && TIME>0) {
				if (System.currentTimeMillis() - start_time > TIME) {
					info.finished = false;
					break loop;
				}
				search_count = 0;
				System.out.println("L:" + number + "\tU:" + maxup + "\ttop stack:" + search.get(maxup).size()
						+ "\toptimistic graphs:" + checking[maxup] + "\ttotal:" + total + " In loop");
			}
		} //loop
		/********** end of loop, a median is found or the running time exceeds the time limit ******************/

		/********** delete tmp files ******************/
		for (int i = 0; i <= top; i++)
			if (file_checking[i] > 0) {
				for (int k = 0; k <= file_checking[i]; k++) {
					name = "TMPR/tmp" + i + "_" + k;
					file = new File(name);
					file.delete();
				}
			}

		/********** set the cycle number or the largest possible one the program finds ******************/
		info.cycle_number = number;
		info.distance=3*info.gene_number-info.cycle_number;
		info.time = System.currentTimeMillis() - start_time;
		info.get_median_genome();
		System.out.println("optima="+info.cycle_number);
		System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("The maximum number of cycles: "+info.cycle_number+"\t\tThe minimum total DCJ distance: "+(3*info.gene_number-info.cycle_number));
		System.out.println("initial bounds: \tL: "+info.lower_bound+"\tu: "+info.upper_bound);
		System.out.println("heuristic: "+info.heuristic2);
		System.out.print("The DCJ distances between leaf genomes and the median: ");
		for(int i=0;i<3;i++) System.out.print(g.gene_size-info.cycle[i]+" ");
		System.out.println();
//		System.out.println("\nThe median genome:");
//		System.out.print(info.median_genome);
		System.out.println("#####################################\n");
		return info;
	}

	public static int heuristic2(Graph g, Solution info) throws Exception {
		// at each AS0 or AS2:m=2, choose the edge which gives the largest upper_bound
		Graph current = new Graph(g);
		EF result = new EF();
		result=jump(current);
		String as=null;
		if(result==null) {
			info.median=new short[2*current.gene_size];
			for(int ii=0;ii<2*current.gene_size;ii++) info.median[ii]=current.lower_median[ii];
			for(int ii=0;ii<3;ii++) info.cycle[ii]=current.lower_cycle[ii];
			if(current.lower_bound>info.maxh) info.maxh=current.lower_bound;
			return current.upper_bound;
		}
//		System.out.println("\nin h2, edge_size "+current.edge_size+"\t cycles: "+current.cycle_number);
		EF result_tmp=null, result_next=null;
		Graph graph_tmp, graph_next;
		int criteria;
		while(true) {
			graph_next=null;
			result_next=null;
			criteria=-99;
			for(ArrayList<Short> black:result.black) {
				graph_tmp=new Graph(current);
				graph_tmp.shrink(black);
				result_tmp=jump(graph_tmp);
				if(graph_tmp.cycle_number>criteria) {
					criteria=graph_tmp.cycle_number;
					graph_next=graph_tmp;
					result_next=result_tmp;
				}
			}
			current=graph_next;
			result=result_next;
			if(result==null) {
				info.median=new short[2*current.gene_size];
				for(int ii=0;ii<2*current.gene_size;ii++) info.median[ii]=current.lower_median[ii];
				for(int ii=0;ii<3;ii++) info.cycle[ii]=current.lower_cycle[ii];
				if(current.lower_bound>info.maxh) info.maxh=current.lower_bound;
				return current.lower_bound;
			}
		}
	}

	public static EF jump(Graph current) {
		while(current.edge_size>0 && current.lower_bound<current.upper_bound && current.lower_bound>current.cycle_number) {
			EF result = new EF();
			EF two = new EF();
			if (Adequate.AS1(current, result)) ;
			else if (Adequate.AS2(current, result)) {
				if (result.black.size() > 1) {
					two = result;
					result = new EF();
					if (Adequate.AS4(current, result)) ;
					else result = two;
				} 
				else ;
			} 
			else if (Adequate.AS4(current, result)) ;
			else Adequate.AS0(current, result);

			if(result.black.size()==1) {
				if(result.black.size()==1) {
					if(result.info=="AS1") {current.as1+=(result.black.get(0).size()/2);}
					else if(result.info=="AS4") {current.as4+=(result.black.get(0).size()/2);}
					else {current.as2+=(result.black.get(0).size()/2);}
				}
				current.shrink(result.black.get(0));
			}
			else return result;
		}
		return null;
	}
}
