

import java.util.ArrayList;
import java.lang.Math;

public class Adequate {
    final static short NULL = -1; // unassigned

    public static boolean AS1(Graph g, EF result) {
	boolean valid[]=new boolean[g.vertice_size];
	for(int i=0;i<g.vertice_size;i++) valid[i]=true;

	for (short v = 0; v < g.vertice_size; v++) {
	    if (!valid[v]) continue; // when v is incident to a selected edge
	    short vn[] = g.neighbor[v];
	    if (vn[0] == vn[1] || vn[0] == vn[2]) {
		if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		result.black.get(0).add(v);
		result.black.get(0).add(vn[0]);
		valid[v]=valid[vn[0]] = false;
	    } else if (vn[1] == vn[2]) {
		if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		result.black.get(0).add(v);
		result.black.get(0).add(vn[1]);
		valid[v] =valid[vn[1]]=false;
	    }
	} 
	// end of search

	if (result.black.size() > 0) { // find AS1
	    result.info = "AS1";
	    return true;
	} else return false;
    }

    public static boolean AS2(Graph g, EF result) {
	short four_cycle[] = new short[4]; // to store a four cycle which only contains two colors
	boolean two = false; // whether a four cycle with only two colors has been found
	// here we use two boolean arrays: valid0 and valid
	// valid0 record whether a vertex is incident to a selected edge
	// valid record whether a vertex belongs to an examed subgraph
	boolean valid0[]=new boolean[g.vertice_size];
	for (int i = 0; i < g.vertice_size; i++) valid0[i] = true; // the vertex is invalid only if it is in a discovered AS subgraph.

	// loop1: we check for each color
	for (short color = 0; color < 3; color++) {
	    boolean valid[]=new boolean[g.vertice_size];
	    for (int i = 0; i < g.vertice_size; i++) valid[i] = valid0[i]; // copy from valid0

	    // loop2: we check each vertex under a given color
	    // 'i' is the vertex we start
	    // 'j' is its neighbor via the edge of the given color
	    for (short i = 0; i < g.vertice_size; i++) {
		if (!valid[i]) continue; // the vertex is invalid here if it is in a discovered AS subgraph or has been searched already.
		short j = g.neighbor[i][color];
		if (j >= g.vertice_size || !valid[j]) continue;
		valid[i] = false; valid[j] = false;

		int c1 = (color + 1) % 3, c2 = (color + 2) % 3; // c1 and c2 are the other two colors
		
		// the four vertice neighboring to i and j
		short i1 = g.neighbor[i][c1], i2 = g.neighbor[i][c2];
		short j1 = g.neighbor[j][c1], j2 = g.neighbor[j][c2];

		if (g.neighbor[i1][color] == j2 && valid[i1] && valid[j2]) {
		    // 0 1' 2, 0  --- each number represents an edge of that color
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(i1);
		    result.black.get(0).add(j);
		    result.black.get(0).add(j2);
		    valid0[i] = valid0[j] = valid0[i1] = valid0[j2] = false;
		    valid[i] = valid[j] = valid[i1] = valid[j2] = false;
		} 
		else if (g.neighbor[i2][color] == j1 && valid[i2] && valid[j1]) { 
		    // 0 2' 1, 0
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(i2);
		    result.black.get(0).add(j);
		    result.black.get(0).add(j1);
		    valid0[i] = valid0[j] = valid0[i2] = valid0[j1] = false;
		    valid[i] = valid[j] = valid[i2] = valid[j1] = false;
		} 
		else if(g.neighbor[i1][c2] == j1 && valid[i1] && valid[j1]) { 
		    // 0 1' 1, 2
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(j);
		    result.black.get(0).add(i1);
		    result.black.get(0).add(j1);
		    valid0[i] = valid0[j] = valid0[i1] = valid0[j1]= false;
		    valid[i] = valid[j] = valid[i1] = valid[j1] = false;
		}
		else if(g.neighbor[i2][c1] == j2 && valid[i2] && valid[j2]) { 
		    // 0 2' 2, 1
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(j);
		    result.black.get(0).add(i2);
		    result.black.get(0).add(j2);
		    valid0[i] = valid0[j] = valid0[i2] = valid0[j2] = false;
		    valid[i] = valid[j] = valid[i2] = valid[j2] = false;
		}
	
		else if (g.neighbor[i1][color] == j1 && (i2 == j1 || j2 == i1) && valid[i1] && valid[j1]) { 
		    // 0 1' 1, 0  i2 == j1 || j2 == i1
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(i1);
		    result.black.get(0).add(j);
		    result.black.get(0).add(j1);
		    valid0[i] = valid0[j] = valid0[i1] = valid0[j1] = false;
		    valid[i] = valid[j] = valid[i1] = valid[j1] = false;
		} 
		else if (g.neighbor[i2][color] == j2 && (i1 == j2 || j1 == i2) && valid[i2] && valid[j2]) { 
		    // 0 2' 2, 0  i1 == j2 || j1 == i2
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(i2);
		    result.black.get(0).add(j);
		    result.black.get(0).add(j2);
		    valid0[i] = valid0[j] = valid0[i2] = valid0[j2] = false;
		    valid[i] = valid[j] = valid[i2] = valid[j2] = false;
		} 
		else if (g.neighbor[i1][color] == j1 && g.neighbor[i2][color] == j2 && i1 != j2 && i2 != j1 && valid[i1] && valid[i2] && valid[j1] && valid[j2]) { 
		    // double four 0 1' 1, 0 2' 2, 0
		    if (result.black.size() == 0) result.black.add(new ArrayList<Short>());
		    result.black.get(0).add(i);
		    result.black.get(0).add(j);
		    result.black.get(0).add(i1);
		    result.black.get(0).add(j1);
		    result.black.get(0).add(i2);
		    result.black.get(0).add(j2);
		    valid0[i] = valid0[j] = valid0[i1] = valid0[i2] = valid0[j1] = valid0[j2] = false;
		    valid[i] = valid[j] = valid[i1] = valid[i2] = valid[j1] = valid[j2] = false;
		} 
		else if (result.black.size() == 0 && two == false) {
		    if (g.neighbor[i1][color] == j1 && valid[i1] && valid[j1]) {
			// System.out.println("it's c!");
			two = true;
			four_cycle[0] = i;
			four_cycle[1] = j;
			four_cycle[2] = i1;
			four_cycle[3] = j1;
		    } 
		    else if (g.neighbor[i2][color] == j2 && valid[i2] && valid[j2]) {
			// System.out.println("it's d!");
			two = true;
			four_cycle[0] = i;
			four_cycle[1] = j;
			four_cycle[2] = i2;
			four_cycle[3] = j2;
		    }
		}
	    } // end of loop 2
	} // end of loop 1

	if (result.black.size() > 0 || two) {
	    if (result.black.size() > 0) {
		result.info = "AS2 m=1";
		return true;
	    } else {
		result.black.add(new ArrayList<Short>());
		result.black.add(new ArrayList<Short>());
		result.black.get(0).add(four_cycle[0]);
		result.black.get(0).add(four_cycle[1]);
		result.black.get(0).add(four_cycle[2]);
		result.black.get(0).add(four_cycle[3]);
		result.black.get(1).add(four_cycle[0]);
		result.black.get(1).add(four_cycle[2]);
		result.black.get(1).add(four_cycle[1]);
		result.black.get(1).add(four_cycle[3]);
		result.info = "AS2 m=2";
		return true;
	    }
	} 
	else return false;
    }

    public static boolean AS4(Graph g, EF result) {
	boolean valid0[]=new boolean[g.vertice_size]; // if a vertex belongs to a selected edge, it valid0 value is false
	for(int i=0;i<g.vertice_size;i++) valid0[i]=true;
	
	/**************step 1: to detect AS4 of type 5-3-5 **************/
	boolean valid[]=new boolean[g.vertice_size];
	for(int i=0;i<g.vertice_size;i++) valid[i]=valid0[i];
	
	out_core_535:for(short core=0;core<g.vertice_size;core++) { // core is the vertex we start with
	    if(!valid[core]) continue;
	    for(int c1=0;c1<2;c1++) {
		short p1=g.neighbor[core][c1];
		if(!valid[p1]) continue;
		for(int c2=c1+1;c2<3;c2++) {
		    short p2=g.neighbor[core][c2];
		    if(!valid[p2]) continue;
		    int c3=3-c1-c2;
		    if(g.neighbor[p1][c3]==p2) {
			/************* one triangle detected ****************/
			short[] tria=new short[3]; // this stores the triangle; the vertices are ordered according to their missing colors
			tria[c3]=core; tria[c2]=p1; tria[c1]=p2;
			valid[core]=valid[c1]=valid[c2]=false; 
			// A triangle can be detected three times from its three vertices, but we just need to find it once
			// make these valid false to prevent it being discovered later
			
			short[] po=new short[3]; 
			// the three vertices connected to the three endpoints of the triangle; Point Out
			for(int i=0;i<3;i++) {
			    // to check whether these three vertices are still valid
			    po[i]=g.neighbor[tria[i]][i];
			    if(!valid[po[i]]) continue out_core_535; 
			}
		    
		   	// at this moment we have discovered a valid triangle and its three hands 
			/******************* to detect 5-3-5 first ****************/
			// this include subgraphs 2-3,2-4,2-5,2-6
			for(int co1=0;co1<3;co1++) { // colors extended from Poing Outs
			    // we start from the hand with co1 among these three hands
			    int co2=(co1+1)%3, co3=(co1+2)%3;
			    short po12=g.neighbor[po[co1]][co2], po13=g.neighbor[po[co1]][co3];
			    if(!valid[po12] ||!valid[po13]) continue;
			    if(g.is_connected(po12, po[co2]) & g.is_connected(po13, po[co3])) {
				    /************** find a 5-3-5 AS4 *******************/
				if(result.black.size()==0) result.black.add(new ArrayList<Short>());
				result.black.get(0).add(po[co2]);result.black.get(0).add(po12);
				result.black.get(0).add(po[co3]);result.black.get(0).add(po13);
				result.black.get(0).add(tria[co1]);result.black.get(0).add(po[co1]);
				result.black.get(0).add(tria[co2]);result.black.get(0).add(tria[co3]);
				valid0[po[co2]]=  valid[po[co2]]=false;
				valid0[po12]=     valid[po12]=false;
				valid0[po[co3]]=  valid[po[co3]]=false;
				valid0[po13]=     valid[po13]=false;
				valid0[tria[co1]]=valid[tria[co1]]=false;
				valid0[po[co1]]=  valid[po[co1]]=false;
				valid0[tria[co2]]=valid[tria[co2]]=false;
				valid0[tria[co3]]=valid[tria[co3]]=false;
				result.info+="535 ";
				continue out_core_535; // find a good one and then process next core vertex
			    }
			} // end of color
			continue out_core_535; 
			// find a triangle but no AS4, process next core vertex; once a vertex is in a triangle, it can not belong to another triangle
		    } // end of if it is a triangle
		} // c2
	    } //c1
	} // end out_core_535

	/**************step 2: to detect AS4 of type 3-3-3 or 3-3 other types **************/
	valid=new boolean[g.vertice_size];
	for(int i=0;i<g.vertice_size;i++) valid[i]=valid0[i];

	out_core_333:for(short core=0;core<g.vertice_size;core++) {
	    if(!valid[core]) continue; // core may have only two valid neighbors
	    short[] pI=new short[3]; // vertices of distance 1
	    short[][] pII=new short[3][3];// vertices of distance 2
	    short[][] pIII=new short[3][3];// vertices of distance 3
	    for(int cI=0;cI<3;cI++) {
		pI[cI]=g.neighbor[core][cI];
		for(int cII=0;cII<3;cII++) {
		    if(!valid[pI[cI]] || cII==cI || !valid[g.neighbor[pI[cI]][cII]] || !valid[g.neighbor[g.neighbor[pI[cI]][cII]][cI]]) 
		    {
		    	pII[cI][cII]=NULL;
		    	pIII[cI][cII]=NULL;
		    }
		    else {pII[cI][cII]=g.neighbor[pI[cI]][cII]; pIII[cI][cII]=g.neighbor[pII[cI][cII]][cI];}
		}
	    }
	    /*************check for 3-3-3 ************/
	    for(int cII0=0;cII0<3;cII0++) {
		if(pIII[0][cII0]==NULL) continue;
		for(int cII1=0;cII1<3;cII1++) {
		    if(pIII[1][cII1]==NULL) continue;
		    for(int cII2=0;cII2<3;cII2++) {
			if(pIII[2][cII2]==NULL) continue;
			if(pIII[0][cII0]==pIII[1][cII1] && pIII[0][cII0]==pIII[2][cII2]) {
			    boolean found=false;
			    short co_core=pIII[0][cII0];
			    /*************** find a cadidate of 3-3-3 **************/
			    if(cII0!=cII1 && cII0!=cII2 && cII1!=cII2) { found=true;  result.info+="333_t ";}
			    /**************  find a 3-3-3 ***************/
			    else {
				if(g.is_connected(pI[0],pI[1]) || g.is_connected(pI[0],pI[2]) || g.is_connected(pI[2],pI[1]) || 
				    g.is_connected(pII[0][cII0],pII[1][cII1]) || g.is_connected(pII[0][cII0],pII[2][cII2]) || g.is_connected(pII[2][cII2],pII[1][cII1])) {
					    found=true;
					    result.info+="333_c ";
				    /************ find a 3-3x3 ***************/
				}
			    }
			    if(found) {
				if(result.black.size()==0) result.black.add(new ArrayList<Short>());
				result.black.get(0).add(core);result.black.get(0).add(co_core);
				result.black.get(0).add(pI[0]);result.black.get(0).add(pII[0][cII0]);
				result.black.get(0).add(pI[1]);result.black.get(0).add(pII[1][cII1]);
				result.black.get(0).add(pI[2]);result.black.get(0).add(pII[2][cII2]);
				valid0[core]=        valid[core]=false;
				valid0[co_core]=     valid[co_core]=false;
				valid0[pI[0]]=       valid[pI[0]]=false;
				valid0[pII[0][cII0]]=valid[pII[0][cII0]]=false;
				valid0[pI[1]]=       valid[pI[1]]=false;
				valid0[pII[1][cII1]]=valid[pII[1][cII1]]=false;
				valid0[pI[2]]=       valid[pI[2]]=false;
				valid0[pII[2][cII2]]=valid[pII[2][cII2]]=false;
				continue out_core_333;
			} // end of found
		    } // end of 333
		} // end of cII2
	    } // end of cII1
	} // end of cII0

	// at this stage, we can find at most 3-3 pattern 
	/*************check for 3-3-other ************/
	for(int c1=0;c1<2;c1++) { // start from core with color c1
		for(int c2=c1+1;c2<3;c2++) { // start from core with color c2
		    for(int c1c=0;c1c<3;c1c++) { // start from pI[c1] with color c1c
			if(pIII[c1][c1c]==NULL) continue;
			for(int c2c=0;c2c<3;c2c++) { // start from pI[c2] with c2c
			    if(pIII[c2][c2c]==NULL) continue;
			    if(pIII[c1][c1c]==pIII[c2][c2c]) { 
				/******** find a 3-3 **********/
				short co_core=pIII[c1][c1c];
				short out1=NULL,out2=NULL;
				boolean has_33_other=false;
				int c3=3-c1-c2;
				if(valid[g.neighbor[core][c3]] && valid[g.neighbor[co_core][c3]]) {
					// if there are valid "three hands"-- the vertices incident to core/co_core via the third color
				    out1=g.neighbor[core][c3];
				    out2=g.neighbor[co_core][c3];
				    if(out1==g.neighbor[g.neighbor[core][c1]][3-c1-c1c] && out2==g.neighbor[g.neighbor[co_core][c1]][3-c1-c1c]) { 
					has_33_other=true;  
					result.info+="353_a ";
					// find an instance of 3-3 or 3-4
				    }
				    else if(out1==g.neighbor[g.neighbor[core][c2]][3-c2-c2c] && out2==g.neighbor[g.neighbor[co_core][c2]][3-c2-c2c]) {
				       	has_33_other=true;  
					result.info+="353_b ";
					// find an instance of 3-3 or 3-4
				    }
				}
				if(!has_33_other && c1==c2c && c2==c1c) { // a cycle of size 6
				    /************ try to detect (3-5) **************/
				    short p1=g.neighbor[core][c1], p2=g.neighbor[core][c2];
				    short cop1=g.neighbor[co_core][c1], cop2=g.neighbor[co_core][c2];
				    // p1, p2, cop1, cop2 are guranteed to be valid
				    if(g.neighbor[p1][c3]==p2) {
					out1=g.neighbor[cop1][c3];
					out2=g.neighbor[cop2][c3];
					if(valid[out1] && valid[out2] && g.is_connected(out1, out2)) { 
					    has_33_other=true;  
					    result.info+="355_a ";
					    // find an instance of (3-5)
					}
				    }
				    else if(g.neighbor[cop1][c3]==cop2) {
					out1=g.neighbor[p1][c3];
					out2=g.neighbor[p2][c3];
					if(valid[out1] && valid[out2] && g.is_connected(out1, out2)) {
					    has_33_other=true;
					    result.info+="355_b ";
					    // find an instance of (3-5)
					}
				    }
				}
				if(!has_33_other) {
				    /*********** try to detect (3-1) or (3-2) **************/
				    short p1=g.neighbor[core][c1], p2=g.neighbor[core][c2];
				    short cop1=g.neighbor[co_core][c1], cop2=g.neighbor[co_core][c2];
				    short p1e=g.neighbor[p1][3-c1-c1c], cop2e=g.neighbor[cop2][3-c2-c2c];
				    short p2e=g.neighbor[p2][3-c2-c2c], cop1e=g.neighbor[cop1][3-c1-c1c];
				    out1=p1e; out2=p2e;
				    if(valid[out1] && out1==cop2e && valid[out2] && out2==cop1e) { 
					has_33_other=true;  
					result.info+="5555 ";
					// find an instance of (3-1) or (3-2)
				    }
				}
		    
				    // final to record
				if(has_33_other) {
				if(result.black.size()==0) result.black.add(new ArrayList<Short>());
				result.black.get(0).add(core);result.black.get(0).add(co_core);
				result.black.get(0).add(out1);result.black.get(0).add(out2);
				result.black.get(0).add(pI[c1]);result.black.get(0).add(pII[c1][c1c]);
				result.black.get(0).add(pI[c2]);result.black.get(0).add(pII[c2][c2c]);
				valid0[core]=        valid[core]=false;
				valid0[co_core]=     valid[co_core]=false;
				valid0[out1]=        valid[out1]=false;
				valid0[out2]=        valid[out2]=false;
				valid0[pI[c1]]=      valid[pI[c1]]=false;
				valid0[pII[c1][c1c]]=valid[pII[c1][c1c]]=false;
				valid0[pI[c2]]=      valid[pI[c2]]=false;
				valid0[pII[c2][c2c]]=valid[pII[c2][c2c]]=false;
				continue out_core_333;
				}
			    } // find a 3-3 structure
			} //c2c
		    } //c1c
		} //c2
	    } //c1
	} // out_core_333

	if(result.black.size()==0) return false;
	else {
	    int len=result.black.get(0).size();
	    for(int i=0;i<len-1;i++)
			for( int j=i+1;j<len;j++) {
			    if(result.black.get(0).get(i)==result.black.get(0).get(j)) {
				for(short b:result.black.get(0)) System.out.print(b+" ");
				System.out.println("\n"+result.info);
				System.exit(-1);
			    }
			}
	    result.info="AS4";
		}
		return true;
    }
	
    public static void AS0(Graph g, EF result) {
	for (short i = 1; i < g.vertice_size; i++) {
	    ArrayList<Short> edge=new ArrayList<Short>();
	    edge.add((short) 0);
	    edge.add(i);
	    result.black.add(edge);
	}
	result.info = "AS0";
    }

}
