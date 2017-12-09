package application.model;

import java.util.*;

public class Generator {
	private List<Targettype> waypoints = new ArrayList<Targettype>();
	private double chance;
	
	public Generator(List<Targettype> waypoints, double chance) {
		this.chance = chance;
		this.waypoints = waypoints;
		
	}
	
	/**
	 * @return Mit gegebener Wahrscheinlichkeit ein neues Flugzeug oder null
	 */
	public Plane execute() { 
		if (chance <= Math.random()) {
			return new Plane(this.waypoints, Flughafen.getTime());
		} else {
			return null;
		}
	}
	
}
