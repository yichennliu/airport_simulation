package application.model;

import java.util.*;

public class Flughafen {
	private int maxplanes = 0;
	private ArrayList<Plane> planes = new ArrayList<Plane>();
	private ArrayList<Generator> generators = new ArrayList<Generator>();
	private Map<String, Node> nodes = new TreeMap<String, Node>();
	private static int time = 0;
	
	public Flughafen(int maxplanes, ArrayList<Plane> planes, ArrayList<Generator> generators, Map<String, Node> nodes) {
		this.maxplanes = maxplanes;
		this.planes = planes;
		this.generators = generators;
		this.nodes = nodes;
	}
	
	public Flughafen() {}
	
	public int getMaxplanes() {
		return maxplanes;
	}

	public ArrayList<Plane> getPlanes() {
		return planes;
	}
	public void setPlanes(ArrayList<Plane> planes) {
		this.planes = planes;
	}
	public void addPlane(Plane plane) {
		this.planes.add(plane);
	}
	public void removePlane(Plane plane) {
		this.planes.remove(plane);
	}
	
	public ArrayList<Generator> getGenerators() {
		return generators;
	}
	public void setGenerators(ArrayList<Generator> generators) {
		this.generators = generators;
	}
	public void addGenerator(Generator generator) {
		this.generators.add(generator);
	}
	
	public Map<String, Node> getNodes() {
		return nodes;
	}
	public void setNodes(Map<String, Node> nodes) {
		this.nodes = nodes;
	}
	public void addNode(String name, Node node) {
		this.nodes.put(name, node);
	}
	
	public static int getTime() {
		return time;
	}
	
	public static void increaseTime() {
		time++;
	}
}