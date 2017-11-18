package application.model;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private static double x;
	private static double y;
	private static String name;
	private Enum kind;
	List<Node>toNode = new ArrayList<Node>();
	Node lastNode=null;
	List<Integer>reserved = new ArrayList<Integer>();
	
	public Node() {	
	}
	
	public Node(double x, double y, Enum kind, String name) {	
		this.x= x;
		this.y=y;
		this.kind=kind;
		
	}
	
	public Node getLastNode() {
		return lastNode;
	}
	
	public void setLastNode(Node lastNode) {
		this.lastNode = lastNode;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}
}
