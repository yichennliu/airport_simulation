package application.model;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class Plane {

	private List<Targettype> waypoints = new ArrayList<Targettype>();
	private int currentTargetWaypointIndex = 0;
	private Tuple<Node,Node> currentNodes = new Tuple<Node, Node>(null, null); // links = last
	
	private int inittime;
	
	public Plane(List<Targettype> waypoints, int inittime) {
		this.waypoints = waypoints;
		this.inittime = inittime;
	}
	
	public List<Targettype> getWaypoints() {
		return this.waypoints;
	}
	
	/**
	 * @return target waypoint, or null if there is no next target
	 */
	public Targettype getCurrentTarget() {
		if (this.currentTargetWaypointIndex < this.getWaypoints().size()) {
			return getWaypoints().get(this.currentTargetWaypointIndex);
		}
		return null;
	}
	
	/**
	 * set next target waypoint
	 * 
	 * @return true of there is a next target, false otherwise
	 */
	public boolean increaseCurrentTarget() {
		if (this.currentTargetWaypointIndex < this.getWaypoints().size()-1) {
			this.currentTargetWaypointIndex++;
			return true;
		}
		return false;
	}
	
	public int getInittime() {
		return this.inittime;
	}
	
	public Node getLastNode() {
		return this.currentNodes.fst();
		
	}
	public Node getNextNode() {
		return this.currentNodes.snd();
	}
	
	/**
	 * Sets the next Node in the plane (and moves the current next node to lastNode!)
	 * @param node to be set
	*/
	public void setNextNode(Node node) {
		Node currentNext = this.currentNodes.snd();
		this.currentNodes.setSnd(node);
		this.currentNodes.setFst(currentNext);
	}
}
