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
		
		// TODO: prüfen ob this.maxplanes überschritten wird, überzählige Flugzeuge in den nächsten Tick verschieben

		if (newPlanes!=null) {// finde für jedes neue Flugzeug einen Weg
			for(int i = 0; i < newPlanes.size(); i++) {
				Plane plane = newPlanes.get(i);
				boolean success = PathFinder.searchFirstWaypoint(this.getNodes(), plane, Flughafen.getTime());
				if (success) {
					// Wenn kein Pfad gefunden wurde: im nächsten Tick noch mal versuchen
					this.removePlane(plane, getTime());
					this.addPlane(plane, getTime()+1);
				}
			}
		}
		
		for(Node node: nodes.values()) { // jeden Node updaten
			node.update(this.getNodes());
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
	
	/**
	 * add plane at a custom time
	 * 
	 * @param plane
	 * @param time
	 */
	public void addPlane(Plane plane, int time) {
		List<Plane> planeList = this.planes.get(time);
		if(planeList==null) this.planes.put(time, new ArrayList<Plane>());
		this.planes.get(time).add(plane);
	}
	
	/**
	 * add plane at its predefined init time
	 * 
	 * @param plane
	 */
	public void addPlane(Plane plane) {
		this.addPlane(plane, plane.getInittime());
	}
	
	/**
	 * Try to remove a plane
	 * 
	 * @param plane The plane
	 * @param time The start time
	 * @return true on success, false otherwise
	 */
	public boolean removePlane(Plane plane, int time) {
		List<Plane> planeList = this.planes.get(time);
		
		if (planeList != null && planeList.contains(plane)) {
			planeList.remove(plane);
			if (planeList.isEmpty()) {
				this.planes.remove(time);
			}
			return true;
		}
		return false;
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