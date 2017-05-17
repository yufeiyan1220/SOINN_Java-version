import java.util.*;
import java.io.*;
import java.util.Map.Entry;
public class SOINN_algorithms {
	private int data_dimension;
	private ArrayList<Node> A_instance;
	private int agemax;
	private ArrayList<Double> thresholds;
	private int lamda;
	private int numofdata;
	private double c;
	
	public void set_lamda(int lamda){
	  this.lamda = lamda;
	  }
	public void set_agemax(int agemax){
	  this.agemax = agemax;
	  }
	public void set_c(double c){
		  this.c = c;
		  }
	
	// Initiate the SOINN 
	public SOINN_algorithms(Node node1, Node node2){
		data_dimension = node1.getdata_dimension();
		A_instance = new ArrayList<Node>();
		A_instance.add(node1);
		A_instance.add(node2);
		thresholds = new ArrayList<Double>();
		thresholds.add(node1.distance(node2));
		thresholds.add(node1.distance(node2));		
		numofdata = 2;
		agemax = 50;
		c = 0.5;
		lamda = 100;
	}
	
	public ArrayList<Node> getA_instance(){
		return A_instance;
	}

	// Implement the algorithms
	protected void SOINN_Learning(Node ksi){
		
		numofdata++;
		// Find s1 and s2
		min_distance winner_dis;
		winner_dis = winner_index(ksi);
		int index1 = winner_dis.index1;
		int index2 = winner_dis.index2;
		
		if(winner_dis.dis > thresholds.get(index1) || winner_dis.dis2 > thresholds.get(index2)){
			A_instance.add(ksi);
			thresholds.add(1000000.0);
		}
		else{
			// update or create a connection 			
			A_instance.get(index1).connection.put(A_instance.get(index2), 1);
			A_instance.get(index2).connection.put(A_instance.get(index1), 1);
			
			// Increase the age of index1 and other nodes connected
			int tempvalue = 0;
			Set<Node> a = A_instance.get(index1).connection.keySet();
			ArrayList<Node> remove_node = new ArrayList();
			for(Node s : a){
				int tem = A_instance.indexOf(s);
				//System.out.println(A_instance.get(index1).c); 
				tempvalue = A_instance.get(index1).connection.get(s);
				tempvalue++;
				A_instance.get(index1).connection.put(s, tempvalue);
				A_instance.get(tem).connection.put(A_instance.get(index1), tempvalue);
				if(A_instance.get(index1).connection.get(s) > agemax){
					remove_node.add(s);					
					A_instance.get(tem).connection.remove(A_instance.get(index1));					
				}				
			}
			for(Node s : remove_node){
				A_instance.get(index1).connection.remove(s);
			}
			A_instance.get(index1).acmlted_number++; 
			A_instance.set(index1, A_instance.get(index1).plus(ksi)); 			
		}
		
		Node temp1 = A_instance.get(index1);
		Node temp2 = A_instance.get(index2);
		// Learning thresholds
		if(temp1.connection.isEmpty()){
			thresholds.set(index1, temp1.distance(temp2));
			//System.out.println(temp1.distance(temp2));
		}
		else{
			Set<Node> a = A_instance.get(index1).connection.keySet();
			double[] distance = new double[a.size()];
			double max_distance = 0;
			int i = 0;
			for(Node s : a){
				distance[i] = temp1.distance(s);
				//System.out.println(distance[i]);
				if(distance[i] >= max_distance){
					max_distance = distance[i];
				}
				i++;
			}
			thresholds.set(index1, max_distance);
			//System.out.println(thresholds);
		}
		
		if(temp2.connection.isEmpty()){
			thresholds.set(index2, temp1.distance(temp2));
		}
		else{
			Set<Node> a = A_instance.get(index2).connection.keySet();
			double[] distance = new double[a.size()];
			double max_distance = 0;
			int i = 0;
			for(Node s : a){
				distance[i] = temp2.distance(s);
				if(distance[i] >= max_distance){
					max_distance = distance[i];
				}
				
				i++;
			}
			thresholds.set(index2, max_distance);
			//System.out.println(thresholds);
		}
		//denoise

		
		if(numofdata % lamda == 0){
			
			int sum_m =0;
			int numofnode = A_instance.size();
			ArrayList<Integer> numofneighbor = new ArrayList<Integer>();
			for(int i = 0; i < numofnode; i++){
				sum_m+= A_instance.get(i).acmlted_number;
				
			}
			double mean = sum_m/(double)numofnode;
			//System.out.println(mean);
			for(int i = 0; i < numofnode; i++){
				numofneighbor.add(A_instance.get(i).connection.size());
			}
			for(int i = 0; i < numofnode; i++){
				if((numofneighbor.get(i) == 1 && A_instance.get(i).acmlted_number < mean*c) || (numofneighbor.get(i) == 0 && A_instance.get(i).acmlted_number < mean)){
					Node temp = A_instance.get(i);
					A_instance.get(i).connection = null;
					A_instance.remove(i);
					for(Node s : A_instance){
						if(s.connection.containsKey(temp)){
							s.connection.remove(temp);
						}
					}
					
					numofneighbor.remove(i);
					thresholds.remove(i);
					i--;
					numofnode--;
					
					
				}
			}
			
		}
	}
	
	
	protected min_distance winner_index(Node ksi){
		int index1 = 0;
		int index2 = 0;
		double min1 = ksi.distance(A_instance.get(0));
		for(int i = 1; i < A_instance.size(); i++){
			if(ksi.distance(A_instance.get(i)) < min1){
				min1 = ksi.distance(A_instance.get(i));
				index1 = i;
			}			
		}
		
		double min2 = ksi.distance(A_instance.get(0));
		if(min2 == min1) min2 = ksi.distance(A_instance.get(1));
		for(int i = 1; i < A_instance.size(); i++){	
			if(ksi.distance(A_instance.get(i)) <= min2 && i != index1){
				min2 = ksi.distance(A_instance.get(i));
				index2 = i;
			}
		}
		min_distance dis = new min_distance(min1, min2, index1, index2);
		return dis;
	}
		
	private class min_distance{
		protected double dis;
		protected double dis2;
		protected int index1;
		protected int index2;
		public min_distance(double dis, double dis2, int index1, int index2){
			this.dis = dis;
			this.dis2 = dis2;
			this.index1 = index1;
			this.index2 = index2;						
		}
	}
	
	public static void main(String[] args) throws IOException{
		// Read data
		String filename = "test.txt";
		ArrayList<String> rows = new ArrayList<String>();
		
		File file = new File(filename);
		BufferedReader input = new BufferedReader(new FileReader(file));
		String tempstring =null;
		int line = 0;
		
		while((tempstring = input.readLine()) != null ){
			rows.add(tempstring);
			line++;
		}
		// learning process
		String[] temp1 =  rows.get(0).split("\t");
		String[] temp2 = rows.get(1).split("\t");
		
		double[] tempnum1 = new double[temp1.length];
		double[] tempnum2 = new double[temp1.length];
		for(int i =0; i < temp1.length; i++){
			tempnum1[i] = Double.parseDouble(temp1[i]);
			tempnum2[i] = Double.parseDouble(temp2[i]);
		}
		Node node1 = new Node(tempnum1);
		Node node2 = new Node(tempnum2);
		
		SOINN_algorithms A = new SOINN_algorithms(node1,node2);
		/* optional (the initial parameter: agemax = 50; c = 0.5; lamda =100)
		A.set_agemax(agemax);
		A.set_lamda(lamda);
		A.set_c(c);
		*/
		
		String[] temp = null;
		double[] tempnum = new double[temp1.length];
		
		
		for(int i = 2; i < line; i++){
			temp = rows.get(i).split("\t");
			for(int j = 0 ; j < temp.length; j++){
				 tempnum[j] = Double.parseDouble(temp[j]);				 
			}
			Node ksi = new Node(tempnum);
			A.SOINN_Learning(ksi);
			//System.out.println(A.thresholds);
			
		}
		System.out.println(A.A_instance.size());				
		// output the data after clustering
		
		String filename_out = "s.txt";
		BufferedWriter output = new BufferedWriter(new FileWriter(new File(filename_out)));
		int i = 0;
		for(Node s : A.getA_instance()){
			output.write(A.getA_instance().get(i).toString());
			output.newLine();
			i++;
		}
		
		output.close();
		
		/*
  		double[] a = {1,2};
		double[] b = {3,4};
		double[] c = {9,9};
		Node node1 = new Node(a);
		Node node2 = new Node(b);
		Node node3 = new Node(c);
		SOINN_algorithms A = new SOINN_algorithms(node1,node2);
		System.out.println(A.thresholds);
		System.out.println(A.getA_instance().get(1).acmlted_number);
		System.out.println(A.winner_index(node3).dis+" "+A.winner_index(node3).index1);
		A.SOINN_Learning(node3);
		System.out.println(A.getA_instance().get(2).acmlted_number);
		*/	
	}
}
