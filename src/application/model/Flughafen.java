package application.model;

import java.util.*;

public class Flughafen {
	private int maxplanes;
	private List<Plane> planes;
	private List<Generator> generators;
	private Map<String, Node> nodes;
	private static int time = 0;
	
	public Flughafen(int maxplanes, List<Plane> planes, List<Generator> generators, Map<String, Node> nodes) {
		this.maxplanes = maxplanes;
		this.planes = planes;
		this.generators = generators;
		this.nodes = nodes;
		PathFinder.startSearch(nodes.values(), planes.get(0), 0);
	}
	
	public int getMaxplanes() {
		return this.maxplanes;
	}

	public List<Plane> getPlanes() {
		return this.planes;
	}
	public void addPlane(Plane plane) {
		this.planes.add(plane);
	}
	public void removePlane(Plane plane) {
		this.planes.remove(plane);
	}
	
	public List<Generator> getGenerators() {
		return this.generators;
	}
	
	public Collection<Node> getNodes() {
		return this.nodes.values();
	}
	public Node getNode(String name) {
		return this.nodes.get(name);
	}
	
	public static int getTime() {
		return time;
	}
	public static void tick() {
		time++;
	}
}