package application.model;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class Plane {

	private List<Targettype> waypoints = new ArrayList<Targettype>();
	private Pair<Node,Node> currentNodes = new Pair<Node, Node>(null, null); // links = last
	
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
		return this.currentNodes.getKey();
	}
	public Node getNextNode() {
		return this.currentNodes.getValue();
	}
	
}
