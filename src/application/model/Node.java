package application.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Node {

	private double x;
	private double y;
	private String name;
	private Kind kind;
	private Map<String, Node> to;
	private Map<String, Node> conflicts;
	private Targettype targettype = null;
	private int waittime;
	private Map<Integer,Tuple<Plane, Boolean>> reserved = new HashMap<Integer,Tuple<Plane, Boolean>>();
	private Tuple <Integer,Plane> blockedBy = null;
	
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
	
	/**
	 * @return Nodes, die von diesem Node aus angeflogen werden können
	 */
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
	
	/**
	 * @return Die Mindestwartezeit für diesen Node
	 */
	public int getWaittime() {
		return this.waittime;
	}
	
	/**
	 * Glibt die Reservierungsinformationen zu diesem Node zurück. 
	 * Der Blockierungsstatus wird separat gehandhabt
	 */
	public Map<Integer,Tuple<Plane,Boolean>> getReserved() {
		return this.reserved;
	}
	
	public void putReserved(Integer time,Plane plane,Boolean stay) {
		this.reserved.put(time, new Tuple<Plane, Boolean>(plane, stay));
	}
	
	/**
	 * Block this node permanently, starting at a given time
	 * 
	 * @param plane The plane to block this node with
	 * @param blockingTime Zeit, ab der der Node blockiert werden soll
	 */
	public void setBlockedBy(Plane plane,Integer blockingTime) {
		this.blockedBy = new Tuple(blockingTime,plane);
		System.out.println(this.name +" blockiert");
	}
	
	/**
	 * Blockierung aufheben
	 */
	public void unblock() {
		this.blockedBy = null;
		System.out.println(this.name +": Blockierung aufgehoben");
	}
	
	/**
	 * The Plane by which this node is blocked, or null
	 * @return
	 */
	public Tuple<Integer,Plane>  getBlockedBy() {
		return this.blockedBy;
	}
	
	/**
	 * @param time Zeit, zu oder nach der auf Blockierung geprüft werden soll
	 * @return true wenn der Node blockiert ist, sonst false
	 */
	public boolean isBlockedAfter(Integer time) {
		if(this.blockedBy!=null)
			return (this.blockedBy.fst()<=time);
		else return false;
	}
	
	public boolean isBlocked() {
		return this.blockedBy!=null;
	}
	
	/**
	 * Check whether the current node is free.
	 * 
	 * A node is free iff
	 * <ul>
	 *  <li>it is not reserved at the given time</li>
	 *  <li>it conflict nodes are not reserved at the given time</li>
	 *  <li>it is not blocked at the given time</li>
	 * </ul>
	 * 
	 * @param time Time to check for
	 * @return true if a plane can land on this node on the given time, false otherwise
	 */
	public boolean isFree(int time) {
		if (this.getReserved().get(time) != null || this.isBlockedAfter(time)) {
			return false;
		} else {
			for (Node conflictNode: this.getConflicts()) {
				if (conflictNode.getReserved().get(time)!=null) {
					return false;
				}
			}
			return true;
		}
	}
		
	/**
	 * @return The plane currently sitting on this node, or null
	 */
	public Plane getPlane() {
		Tuple<Plane, Boolean> reservation = reserved.get(Flughafen.getTime());
		if (reservation != null && reservation.snd()) {
			return reservation.fst();
		} 
		else {
			// return plane if node is blocked, or null
			if(this.blockedBy!=null) {
				if(Flughafen.getTime()>=this.blockedBy.fst())
					return this.blockedBy.snd();
			}
		}
		return null;
	}
}
