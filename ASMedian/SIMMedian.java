

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class SIMMedian {

	public static void main(String[] args) throws Exception {
		ArrayList<Parameter> pp = new ArrayList<Parameter>(); // the list containing all parameter information
		File file;
		BufferedWriter bw;
		BufferedReader br;
		
		/********** the only parameter is ued to specify the name of the file which contains all paremeter information ******************/
		if (args.length >= 1) {
			file = new File(args[0]);
			if (!file.exists()) {
				System.out.println("File not exist " + args[0]);
				System.exit(1);
			}
			br = new BufferedReader(new FileReader(file));
			String s;
			while ((s = br.readLine()) != null) {
				// System.out.println(s+"\t"+s.length());
				pp.add(new Parameter(s));
			}
			br.close();
		} else {
			System.out.println("No parameter");
			System.exit(1);
		}

		file = new File("TMPR");
		if (!file.exists())
			file.mkdir();
		file = new File("result");
		if (!file.exists())
			file.mkdir();
		file = new File("MBG");
		if (!file.exists())
			file.mkdir();
		file = new File("Genomes");
		if (!file.exists())
			file.mkdir();

		ObjectInputStream oin;
		ObjectOutputStream oout;
		for (Parameter p : pp) {
			System.out.println(p.gene_number + "\t" + p.reversal);
			ArrayList<Graph> graphs = new ArrayList<Graph>(p.repeat);
			String name,name2;
			if(p.sym) {
				name = "MBG/MBG_" + p.gene_number + "_sym_" + p.reversal + "_" + p.repeat;
				name2 = "Genomes/Genomes_" + p.gene_number + "_sym_" + p.reversal + "_" + p.repeat;
			}
			else {
				name = "MBG/MBG_" + p.gene_number + "_asym_" + p.e1+"_"+p.e2+"_"+p.e3 + "_" + p.repeat;
				name2 = "Genomes/Genomes_" + p.gene_number + "_asym_" + p.e1+"_"+p.e2+"_"+p.e3 + "_" + p.repeat;
			}
			File file2=new File(name2);
			boolean write_out=false;
			if(!file2.exists()) {file2.mkdir();write_out=true;}
			file = new File(name);

			if (file.exists()) {
				oin = new ObjectInputStream(new BufferedInputStream(
						new FileInputStream(file)));
				for (int i = 0; i < p.repeat; i++) {
					Graph g=new Graph((IOCircular) oin.readObject());
					graphs.add(g);
					File file3=new File(name2+"/"+(i+1)+".genome");
					PrintWriter gout=new PrintWriter(file3);
					gout.write(Chromosome.Convert.Graph_2_Genome(g));
					gout.close();
				}
				oin.close();
			} else {
				oout = new ObjectOutputStream(new BufferedOutputStream(
						new FileOutputStream(file)));
				Random rd=new Random();
				for (int i = 0; i < p.repeat; i++) {
					Chromosome c[] = {
							new Chromosome(p.gene_number),
							new Chromosome(p.gene_number),
							new Chromosome(p.gene_number) };
					if(p.sym) {
						c[0].reverse(p.reversal);
						c[1].reverse(p.reversal);
						c[2].reverse(p.reversal);
					}
					else {
						c[0].reverse(p.e1);
						c[1].reverse(p.e2);
						c[2].reverse(p.e3);
					}
					
					IOCircular iog=new IOCircular(c);
					Graph g = new Graph(iog);
					graphs.add(g);
					oout.writeObject(iog);
					File file3=new File(name2+"/"+(i+1)+".genome");
					PrintWriter gout=new PrintWriter(file3);
					gout.write(Chromosome.Convert.Graph_2_Genome(g));
					gout.close();
				}
				oout.close();
			}

			Solution sl;
			long average_time = 0;
			long average_h_time2 = 0, average_h_time3 = 0, average_h_time4 = 0, average_h_time3_two = 0, average_h_time4_two = 0;;
			int success = 0;
			int item = 0;
			String fname1,fname2;
			if(p.sym) {
				fname1 = "result/result_" + p.gene_number + "_sym_" + p.reversal + "_" + p.repeat;
				fname2 = "result/detail_" + p.gene_number + "_sym_" + p.reversal + "_" + p.repeat;
			}
			else {
				fname1 = "result/result_" + p.gene_number + "_asym_"  + p.e1+"_"+p.e2+"_"+p.e3 + "_" + p.repeat;
				fname2 = "result/detail_" + p.gene_number + "_asym_"  + p.e1+"_"+p.e2+"_"+p.e3 + "_" + p.repeat;
			}
			file=new File(fname2);
			if(!file.exists()) 
				file.mkdir();
			PrintWriter wr = new PrintWriter(fname1);
			float plasmid=0;
			float diff2=0, diff3=0, diff4=0, diff3_two=0, diff4_two=0;
			float diffp2=0, diffp3=0, diffp4=0, diffp3_two=0, diffp4_two=0;
			float ddiffp2=0, ddiffp3=0, ddiffp4=0, ddiffp3_two=0, ddiffp4_two=0;
			int max_diff2=0, max_diff3=0, max_diff4=0, max_diff3_two=0, max_diff4_two=0;
			float max_diffp2=0, max_diffp3=0, max_diffp4=0, max_diffp3_two=0, max_diffp4_two=0;
			float max_ddiffp2=0, max_ddiffp3=0, max_ddiffp4=0, max_ddiffp3_two=0, max_ddiffp4_two=0;
			int exact2=0, exact3=0, exact3_two=0, exact4=0, exact4_two=0;
			int solved_exact2=0, solved_exact3=0, solved_exact3_two=0, solved_exact4=0, solved_exact4_two=0;
			float as22=0,as0=0,as1=0,as2=0,as4=0;
			for (Graph g : graphs) {
				System.out.println("\n#############     "+(++item)+"/"+p.repeat+" ("+p.gene_number+","+p.reversal+","+p.e1+","+p.e2+","+p.e3+")"+ "     #############");
//				for (int i = 0; i < g.v_size; i++) {
//					System.out.print(i + ": ");
//					for (int color = 0; color < 3; color++)
//						System.out.print(g.neighbor[i][color] + " ");
//					System.out.println();
//				}
				sl = ASMedian.solver(g);
				wr.println(sl.toString());
//				for (Short b : sl.median)
//					wr.print(b + " ");
//				wr.println();
				wr.flush();
				PrintWriter wr_dt=new PrintWriter(fname2+"/"+item+".median");
				wr_dt.write(sl.median_genome);
				wr_dt.println("#####################################\n");
				if(sl.finished) wr_dt.println("finished");
				else wr_dt.println("unfinished");
				wr_dt.printf("maximum cycle number:\t %10d \tminimum DCJ distance:\t %10d\n", sl.cycle_number,(3*sl.gene_number-sl.cycle_number));
				wr_dt.printf("initial lower bounds:\t %10d \tinitial upper bound:\t %10d\n",sl.lower_bound, sl.upper_bound);
				wr_dt.print("\n");
				wr_dt.print("                                              h2         h3     h3_two         h4     h4_two \n");
				wr_dt.printf("heuristic cycle numbers : \t\t%10d\n",sl.heuristic2);
				wr_dt.printf("differences with the optimal: \t\t%10d\n",(sl.cycle_number-sl.heuristic2));
				wr_dt.printf("heuristic running times: \t\t%10d\n",sl.h_time2);
				wr_dt.print("\n");
				wr_dt.printf("The DCJ distances between leaf genomes and the median: \t%10d %10d %10d\n",(g.gene_size-sl.cycle[0]),(g.gene_size-sl.cycle[1]),(g.gene_size-sl.cycle[2]));
				wr_dt.close();
				average_h_time2 += sl.h_time2;
//				if (sl.finished) {
					int df2=sl.cycle_number-sl.heuristic2;
					if(df2==0) exact2++;
					if(sl.finished) {
						if(df2==0) solved_exact2++;
					}
					float dfp2=(float)(df2*1.0/sl.cycle_number);
					float ddfp2=(float)(df2*1.0/sl.distance);
					diff2+=df2;
					diffp2+=dfp2;
					ddiffp2+=ddfp2;
					if(df2>max_diff2) max_diff2=df2;
					if(dfp2>max_diffp2) max_diffp2=dfp2;
					if(ddfp2>max_ddiffp2) max_ddiffp2=ddfp2;
					average_time += sl.time;
					if(sl.finished) {success++;plasmid+=sl.circular;}
//					System.out.println("cycle number=" + sl.cycle_number);
				}
//			}
//			if (success == 0) {
//				average_time = -1;
//				diff2=-1;diff3=-1;diff4=-1;diff3_two=-1;diff4_two=-1;
//				diffp2=-1;diffp3=-1;diffp4=-1;diffp3_two=-1;diffp4_two=-1;
//				ddiffp2=-1;ddiffp3=-1;ddiffp4=-1;ddiffp3_two=-1;ddiffp4_two=-1;
//			}
//			else {
				average_time /= graphs.size();
				diff2/=graphs.size();diff3/=graphs.size();diff4/=graphs.size();diff3_two/=graphs.size();diff4_two/=graphs.size();
				diffp2/=graphs.size();diffp3/=graphs.size();diffp4/=graphs.size();diffp3_two/=graphs.size();diffp4_two/=graphs.size();
				ddiffp2/=graphs.size();ddiffp3/=graphs.size();ddiffp4/=graphs.size();ddiffp3_two/=graphs.size();ddiffp4_two/=graphs.size();
//			}
			average_h_time2 /= graphs.size();
			average_h_time3 /= graphs.size();
			average_h_time4 /= graphs.size();
			average_h_time3_two /= graphs.size();
			average_h_time4_two /= graphs.size();

			plasmid/=success;
			wr.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			wr.printf("number of cases solved:\t%6d\t\t\t\taverage number of plasmids:\t%10.2f\n",success,plasmid);
			wr.printf("average time (millisecond): %14d\t\taverage heuristic time: (millisecond) %14d\n",average_time, average_h_time2);
			wr.close();
		}
		file = new File("TMPR");
		if(file.exists()) file.delete();
	}
}
