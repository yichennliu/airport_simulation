package application.model;

import java.util.ArrayList;
import java.util.List;

public class Plane {

	private List<Targettype> waypoints = new ArrayList<Targettype>();
	private int inittime;
	private Node lastNode = null;
	
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
		return this.getLastNode();
	}
	
}
