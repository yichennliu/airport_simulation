package application.model;

import java.util.*;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public class Node {

	private double x;
	private double y;
	private String name;
	private Kind kind;
	private Map<String, Node> to;
	private Map<String, Node> conflicts;
	private Targettype targettype = null;
	private int waittime;
	private List<Integer> reserved = new ArrayList<Integer>();
	
	public Node(double x, double y, String name, Kind kind, @NotNull Map<String, Node> to, 
			@NotNull Map<String, Node> conflicts, @Nullable Targettype targettype, int waittime) {	
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
	
	@NotNull
	public Collection<Node> getTo() {
		return this.to.values();
	}
	
	/**
	 * Only for import
	 */
	public void addToNode(Node node) {
		this.to.put(node.getName(), node);
	}

	@NotNull
	public Collection<Node> getConflicts() {
		return this.conflicts.values();
	}
	
	/**
	 * Only for import
	 */
	public void addConflict(Node conflict) {
		this.conflicts.put(conflict.getName(), conflict);
	}

	@Nullable
	public Targettype getTargettype() {
		return this.targettype;
	}
	
	public int getWaittime() {
		return this.waittime;
	}
	
	public List<Integer> getReserved() {
		return this.reserved;
	}
	
}
