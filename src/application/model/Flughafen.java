package application.model;

import java.util.*;

public class Flughafen {
	private int maxplanes;
	private Map<Integer,List<Plane>> planes; // eine Map, die eine Liste der zu spawnenden Flugzeuge zu bestimmten Zeitpunkten hält
	private List<Generator> generators;
	private Map<String, Node> nodes;
	private static int time = 0;
	
	public Flughafen(int maxplanes, List<Plane> planes, List<Generator> generators, Map<String, Node> nodes) {
		this.maxplanes = maxplanes;
		this.planes = new HashMap<Integer,List<Plane>>();
		for(Plane plane: planes) addPlane(plane);
		this.generators = generators;
		this.nodes = nodes;
	}
	
	public void update() {
		
		for(Generator g:this.generators) { // Generatoren ausführen
			Plane plane = g.execute();
			if(plane!=null) this.addPlane(plane);		
		}
		
		List<Plane> newPlanes = this.planes.get(Flughafen.time); // die Flugzeuge, die in diesem Tick erzeugt werden sollen

		if(newPlanes!=null) // finde für jedes neue Flugzeug einen Weg
			for(Plane plane: newPlanes) PathFinder.startSearch(nodes.values(), plane, Flughafen.time);
		
		for(Node node: nodes.values()) { // jeden Node updaten
			node.update();
		}
	}
	
	public int getMaxplanes() {
		return this.maxplanes;
	}

	public Collection<Plane> getPlanes() {
		Collection<Plane> collector = new ArrayList<Plane>();
		for(List<Plane> planeList: this.planes.values()) {
			for(Plane plane:planeList) {
				collector.add(plane);
			}
		}
		return collector;
	}
	
	public void addPlane(Plane plane) {
		List<Plane> planeList = this.planes.get(plane.getInittime());
		if(planeList==null) this.planes.put(plane.getInittime(), new ArrayList<Plane>());
		this.planes.get(plane.getInittime()).add(plane);
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