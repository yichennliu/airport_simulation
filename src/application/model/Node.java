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
	
	public Collection<Node> getTo() {
		return this.to.values();
	}
	
	/**
	 * updates the Node and Planes that are at this node by using the current Airport time
	 * 
	 * @param nodes Airport nodes
	 */
	public void update(Collection<Node> nodes) {
		Plane plane = this.getPlane(); // gibt entweder ein Flugzeug zurück oder null (ein Flugzeug, das gerade blockiert oder gerade angekommen ist)
		if (plane != null) { 
			System.out.println("PLane auf Node " + this.name);
			plane.setNextNode(this);
			
			if (this.isBlocked(Flughafen.getTime())) {
				// Flugzeug noch nicht am Ziel, nächsten waypoint suchen
				boolean success = PathFinder.search(nodes, plane, Flughafen.getTime(), this, plane.getCurrentTarget());
				if (success) {
					// Flugzeug kann weiterfliegen, Blockierung aufheben
					this.unblock();
				} else {
					// Blockierung beibehalten.
				}
			} else {
				// Flugzeug vorhanden aber Node nicht blockiert: Flugzeug am Ziel
				// TODO: Alte Flugzeuge aus Flughafen löschen um mit maxplanes vergleichen zu können?
			}
		}
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
	
	
	public Map<Integer,Tuple<Plane,Boolean>> getReserved() {
		return this.reserved;
	}
	
	public void putReserved(Integer time,Plane plane,Boolean stay) {
		this.reserved.put(time, new Tuple<Plane, Boolean>(plane, stay));
	}
	
	/**
	 * Block this node permanently, or unblock it (set it to null)
	 * 
	 * @param plane The plane to block this node with
	 */
	public void setBlockedBy(Plane plane,Integer blockingTime) {
		this.blockedBy = new Tuple(blockingTime,plane);
	}
	
	public void unblock() {
		this.blockedBy = null;
	}
	
	/**
	 * The Plane by which this node is blocked, or null
	 * @return
	 */
	public Tuple<Integer,Plane>  getBlockedBy() {
		return this.blockedBy;
	}
	
	public boolean isBlocked(Integer time) {
		if(this.blockedBy!=null)
			return (this.blockedBy.fst()<=time);
		else return false;
	}
	
	/**
	 * Check whether the current node is free.
	 * 
	 * A node is free if
	 * <ul>
	 *  <li>it is not reserved at the given time</li>
	 *  <li>it conflict nodes are not reserved at the given time</li>
	 *  <li>it is not blocked</li>
	 * </ul>
	 * 
	 * @param time Time to check for
	 * @return true if a plane can land on this node on the given time, false otherwise
	 */
	public boolean isFree(int time, Plane plane) {
		boolean blocked = (this.isBlocked(time)) ? (plane!=this.getBlockedBy().snd()) : false;

	
		if (this.getReserved().get(time) != null || blocked) {
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
