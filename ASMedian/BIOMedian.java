

import java.util.ArrayList;
import java.lang.Math;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BIOMedian {

	/**
	 * @param args
	 */
	static int BLOCK = 2000;
	static long TIME = 3600000;

	public static void main(String[] args) throws Exception {
		/********** to specify the file name which contains the data ****************/
		if (args.length < 1) {
			System.out.println("No file specified.");
			System.exit(-1);
		}
		File f = new File(args[0]);
		if (!f.exists()) {
			System.out.println("file not exisits");
			System.exit(-1);
		}

		File files[];
		File path=null;
		boolean is_dir;
		if(f.isDirectory()) {
			files=f.listFiles();
			path=new File(args[0]+"_result");
			if(!path.exists()) path.mkdir();
			is_dir=true;
		}
		else {
			files=new File[1]; 
			files[0]=f;
			is_dir=false;
		}

		File file;
		file=new File("TMPR");
		if(!file.exists()) file.mkdir();


		Pattern ch_start=Pattern.compile("[^-\\d]*");
		Pattern numset=Pattern.compile(".*[>\\d].*");

		/*************** for each file ********************/

		for(File fin:files) {
			if(fin.isDirectory()) continue;
			String file_name=fin.getName();
			BufferedReader bf=new BufferedReader(new InputStreamReader(new FileInputStream(fin)));
			String sin;
			Chromosome chr[]=new Chromosome[3];
			int gcnt=-1; int ccnt=-1;
			
			while((sin=bf.readLine())!=null) {
				Matcher m = numset.matcher(sin);
				if(!m.matches()) continue; 
				if(sin.contains(">")) {
					gcnt++;ccnt=0;
					if(gcnt>=3) {
						System.out.println("There are more than 3 genomes in file "+file_name+". Only the first three are taken!");
						break;
					}
					continue;
				}
				else {
					ccnt++;
					if(ccnt!=1) {
						System.out.println("In file"+file_name+": Each genomes should only contain one circular chromosome and begin with a line containing '>'!");
						System.exit(-1);
					}
					Scanner sins=new Scanner(sin);
					sins.skip(ch_start);
					int gene_cnt=0;
					while(sins.hasNextInt()){ 
						sins.nextInt();
						gene_cnt++;
					}
					sins.close();
					for(int i=0;i<gcnt;i++) 
						if(gene_cnt!=chr[i].size) {
							System.out.println("In file"+file_name+": All genomes should contain the same amount of genes!");
							System.exit(-1);
						}
					int genes[]=new int[gene_cnt];
					sins=new Scanner(sin);
					sins.skip(ch_start);
					gene_cnt=-1;
					while(sins.hasNextInt()) {
						gene_cnt++;
						genes[gene_cnt]=sins.nextInt();
					}
					chr[gcnt]=new Chromosome(genes);
				}
			} 

			Graph g=new Graph(new IOCircular(chr));
			/**************** read in graphs *******************/

			Solution sl;
			PrintWriter wr_dt=null;
			if(is_dir) {
				wr_dt = new PrintWriter(new File(path+"/"+file_name+".rst"));
			}
			else{
				wr_dt = new PrintWriter(new File(file_name+".rst"));
			}
			System.out.println("\n#############     "+file_name+ "     #############");
			sl = ASMedian.solver(g);
			wr_dt.write(sl.median_genome);
			wr_dt.println("#####################################\n");
			wr_dt.printf("maximum cycle number:\t %10d \tminimum DCJ distance:\t %10d\n", sl.cycle_number,(3*sl.gene_number-sl.cycle_number));
			wr_dt.printf("initial lower bounds:\t %10d \tinitial upper bound:\t %10d\n",sl.lower_bound, sl.upper_bound);
			wr_dt.print("\n");
			wr_dt.printf("heuristic cycle numbers : \t\t%10d\n",sl.heuristic2);
			wr_dt.printf("differences with the optimal: \t\t%10d\n",(sl.cycle_number-sl.heuristic2));
			wr_dt.printf("heuristic running times: \t\t%10d\n",sl.h_time2);
			wr_dt.print("\n");
			wr_dt.printf("The DCJ distances between leaf genomes and the median: \t%10d %10d %10d\n",(g.gene_size-sl.cycle[0]),(g.gene_size-sl.cycle[1]),(g.gene_size-sl.cycle[2]));
			wr_dt.close();
		}
		file = new File("TMPR");
		if(file.exists()) file.delete();	
	}
}
