import java.util.ArrayList;
import java.util.HashMap;

public class Node{
		
	private double []a;
	public HashMap<Node,Integer> connection;
	private ArrayList<Node> neibour;
	protected int acmlted_number; 
	private int data_dimension;
	public Node(){
			
	}
		
	public Node(double[] attributes){
		data_dimension = attributes.length;
			
		a = new double[data_dimension];
		for(int i = 0; i < data_dimension; i++){
			a[i] = attributes[i];
		}
		connection = new HashMap<Node,Integer>();
		acmlted_number = 1;
	}
	
	public int getdata_dimension(){
		return data_dimension;
	}
	
	public double distance(Node node){
		double result = 0;
		for(int i = 0; i < data_dimension; i++){
			result+= Math.pow(this.a[i] - node.a[i], 2);
		}
		return Math.sqrt(result);
	}
	public Node plus(Node node_plus){
		
		double[] temp = new double[data_dimension];
		for(int i = 0; i < data_dimension; i++){
			temp[i] = this.a[i] - (1/(double) acmlted_number)*(this.a[i] - node_plus.a[i]);
		}
		for(int i = 0; i < data_dimension; i++){
			this.a[i] = temp[i];
		}
		return this;
	}
	public String toString(){
		StringBuffer A  = new StringBuffer();
		for(int i = 0; i < data_dimension; i++){
			A.append(String.valueOf(a[i])+"\t");
		}
		
		return A.toString();
	}
}
