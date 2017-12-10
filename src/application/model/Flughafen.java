package application.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
		// Ausgeflogene Flugzeuge löschen
		for (Plane plane: this.planesToRemove) {
			plane.setNextNode(null);
			plane.setNextNode(null);
			System.out.println("Remove plane");
		}
		this.planesToRemove.clear();
		
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
					boolean success = PathFinder.searchFirstWaypoint(this.getNodes(), plane, Flughafen.getTime(), plane.getCurrentTarget());
					if (success) {
						this.activePlanes++;
					} else {
						// versuche wait-target anzufliegen
						boolean foundWait = PathFinder.searchFirstWaypoint(this.getNodes(), plane, Flughafen.getTime(), Targettype.WAIT);
						if (foundWait) {
							this.activePlanes++;
						} else {
							// Wenn kein Pfad gefunden wurde: im nächsten Tick noch mal versuchen
							plane.increaseInittime();
						}
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
			plane.setNextNode(node);
			
			Node lastNode = plane.getLastNode();
			String lastNodeName = (lastNode==null)? "N.A" : lastNode.getName();
			
			// falls an einem Ausflug-Knoten angekommen
			if(node.getTargettype()!=null && node.getTargettype().equals(plane.getLastTarget())) {
				this.planesToRemove.add(plane);
				this.activePlanes--;
			} else {
				// falls gerade ein Flugzeug draufsteht, das den Node blockiert
				if (node.isBlockedAfter(Flughafen.getTime())) {
					// auf Mindeswartezeit prüfen
					if (plane.getWaitingDuration() >= node.getWaittime()) {
						// Flugzeug noch nicht am Endziel, nächsten waypoint suchen
						boolean success = PathFinder.search(this.getNodes(), plane, Flughafen.getTime(), node, plane.getCurrentTarget());
						if (success) {
							// Flugzeug kann weiterfliegen, Blockierung aufheben, Wartezeit zurücksetzen
							plane.resetWaitingDuration();
							node.unblock();
						} else if(node.getTargettype()!=Targettype.WAIT) { // nur einen Wait-Knoten suchen, wenn das Flugzeug nicht schon auf einem steht
							// suche freien Wait-Knoten
							success = PathFinder.search(this.getNodes(), plane, Flughafen.getTime(), node, Targettype.WAIT);
							if (success) node.unblock();
							else {
								// Warten
							}
						} else {
							// Warten
						}
					} else {
						// Warten, der Mindeswartezeit annähern
						plane.increaseWaitingDuration();
					}
				}
			}
		}
	}
	
	/**
	 * @return Maximale Anzahl der Flugzeuge
	 */
	public int getMaxplanes() {
		return this.maxplanes;
	}

	public Collection<Plane> getPlanes() {
		return this.planes;
	}
	
	/**
	 * @return Flugzeug-Generatoren
	 */
	public List<Generator> getGenerators() {
		return this.generators;
	}
	
	public Collection<Node> getNodes() {
		return this.nodes.values();
	}
	public Node getNode(String name) {
		return this.nodes.get(name);
	}
	
	/**
	 * @return Aktuelle Zeit des Flughafens
	 */
	public static int getTime() {
		return time;
	}
	public static void tick() {
		time++;
	}
	
	/**
	 * @return Anzahl der auf dem Flughafen befindlichen Flugzeuges
	 */
	public int getActivePlanes() {
		return this.activePlanes;
	}
}