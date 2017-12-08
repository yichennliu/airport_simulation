package application.model;

import java.util.*;

public class Flughafen {
	private int maxplanes;
	private List<Plane> planes;
	private List<Plane> planesToRemove = new ArrayList<Plane>();
	private int activePlanes = 0;
	private List<Generator> generators;
	private Map<String, Node> nodes;
	private static int time = 0;
	
	public Flughafen(int maxplanes, List<Plane> planes, List<Generator> generators, Map<String, Node> nodes) {
		this.maxplanes = maxplanes;
		this.planes = planes;
		this.generators = generators;
		this.nodes = nodes;
	}
	
	/**
	 * Flughafen aktualisieren
	 */
	public void update() {
		for (Plane plane: planesToRemove) {
			plane.setNextNode(null);
			plane.setNextNode(null);
			System.out.println("Remove plane");
		}
		planesToRemove = new ArrayList<Plane>();
		
		// Generatoren ausführen
		for(Generator g:this.generators) {
			Plane plane = g.execute();
			if(plane!=null) this.planes.add(plane);
		}
		
		// die Flugzeuge, die in diesem Tick erzeugt werden sollen
		List<Plane> newPlanes = new ArrayList<Plane>();
		for (Plane plane: this.planes) {
			if (plane.getInittime() == getTime()) newPlanes.add(plane);
		}
		
		// Flugzeuge starten lassen falls Weg frei
		if (!newPlanes.isEmpty()) {
			for(int i = 0; i < newPlanes.size(); i++) {
				Plane plane = newPlanes.get(i);
				if (this.activePlanes < this.maxplanes) {
					boolean success = PathFinder.searchFirstWaypoint(this.getNodes(), plane, Flughafen.getTime());
					if (success) {
						this.activePlanes++;
					} else {
						// Wenn kein Pfad gefinden wurde erreicht: im nächsten Tick noch mal versuchen
						plane.increaseInittime();
					}
				} else {
					// Wenn maxplanes erreicht: im nächsten Tick noch mal versuchen
					plane.increaseInittime();
				}
			}
		}
		
		// jeden Node updaten und Fleugzeuge gefegebenenfalls weiterfliegen lassen
		for(Node node: nodes.values()) {
			this.updateNode(node);
		}
	}
	
	/**
	 * Aktualisiert die Zustände der Nodes und lässt (falls nötig) darauf befindliche Flugzeuge weiterfliegen
	 * 
	 * @param node
	 */
	private void updateNode(Node node) {
		Plane plane = node.getPlane(); // gibt entweder ein Flugzeug zurück oder null (ein Flugzeug, das gerade blockiert oder gerade angekommen ist)
		if (plane != null) { 
			System.out.println("Plane auf Node " + node.getName());
			/* falls an einem Ausflug-Knoten angekommen - noch verbessern!! */
			if(node.getTargettype()!=null && node.getTargettype().equals(plane.getLastTarget())) {
				plane.setNextNode(node);
				this.planesToRemove.add(plane);
				this.activePlanes--;
			} else {
				plane.setNextNode(node);
				
				if (node.isBlockedAfter(Flughafen.getTime())) { // falls gerade ein Flugzeug draufsteht, das den Node blockiert
					// Flugzeug noch nicht am Endziel, nächsten waypoint suchen
					boolean success = PathFinder.search(this.getNodes(), plane, Flughafen.getTime(), node, plane.getCurrentTarget());
					if (success) {
						// Flugzeug kann weiterfliegen, Blockierung aufheben
						node.unblock();
					} else if(node.getTargettype()!=Targettype.wait) { // nur einen Wait-Knoten suchen, wenn das Flugzeug nicht schon auf einem steht
						// suche freien Wait-Knoten
						success = PathFinder.search(this.getNodes(), plane, Flughafen.getTime(), node, Targettype.wait);
						if (success) node.unblock();
						else {
							// Blockierung beibehalten.
						}
						
					}
				}
			}
		}
	}
	
	public int getMaxplanes() {
		return this.maxplanes;
	}

	public Collection<Plane> getPlanes() {
		return this.planes;
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