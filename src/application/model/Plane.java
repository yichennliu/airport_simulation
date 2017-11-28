package application.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Plane {

	private List<Targettype> waypoints = new ArrayList<Targettype>();
	private Tuple<Node,Node> currentNodes; // links = last
	
	private int inittime;
	
	public Plane(List<Targettype> waypoints, int inittime) {
		this.waypoints = waypoints;
		this.inittime = inittime;
	}
	
	public List<Targettype> getWaypoints() {
		return this.waypoints;
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
	
}
