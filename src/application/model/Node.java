package application.model;

import java.util.*;

public class Node {

	private double x;
	private double y;
	private String name;
	private Kind kind;
	private Map<String, Node> to;
	private Map<String, Node> conflicts;
	private Targettype targettype = null;
	private int waittime;
	private Map<Integer,Plane> reserved = new HashMap<Integer,Plane>();
	
	public Node(double x, double y, String name, Kind kind, Map<String, Node> to, 
			 Map<String, Node> conflicts,  Targettype targettype, int waittime) {	
		this.x = x;
		this.y = y;
		this.name = name;
		this.kind = kind;
		this.to = to;
		this.conflicts = conflicts;
		this.targettype = targettype;
		this.waittime = waittime;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Kind getKind() {
		return this.kind;
	}
	
	public Collection<Node> getTo() {
		return this.to.values();
	}
	
	/**
	 * Only for import
	 */
	public void addToNode(Node node) {
		this.to.put(node.getName(), node);
	}

	public Collection<Node> getConflicts() {
		return this.conflicts.values();
	}
	
	/**
	 * Only for import
	 */
	public void addConflict(Node conflict) {
		this.conflicts.put(conflict.getName(), conflict);
	}

	public Targettype getTargettype() {
		return this.targettype;
	}
	
	public int getWaittime() {
		return this.waittime;
	}
	
	
	public Map<Integer,Plane> getReserved() {
		return this.reserved;
	}
	
	public void putReserved(Integer time,Plane plane) {
		this.reserved.put(time, plane);
	}
	
}
