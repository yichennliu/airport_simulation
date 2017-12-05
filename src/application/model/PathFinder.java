package application.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PathFinder {
	
	/**
	 * Suche und reserviere einen Pfad für ein Flugzeug
	 * 
	 * @param nodes Die zu durchsuchenden Nodes
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	 * @return true wenn ein Pfad gefunden wurde, sonst false
	 */
	public static boolean startSearch(Collection<Node> nodes, Plane plane, int starttime) {
		System.out.println(plane.getWaypoints().toString());
		
		// find Nodes with the start target type that are free at starttime
		List<Node> startNodes = new ArrayList<>();	
		for (Node node: nodes) {
			if (node.getTargettype() != null && node.getTargettype().equals(plane.getWaypoints().get(0)) && node.isFree(starttime)) {
				startNodes.add(node);
			}
		}
		
		// Wenn kein Startnode frei
		if (startNodes.isEmpty()) {
			System.out.println("Es wurde kein Weg gefunden :(");
			return false;
		}
		
		// suche Pfade zwischen den einzelnen Waypoints
		Map<Node,Breadcrumb> nodesStatus = null;
		Node result = null;
		for (int i = 1; i < plane.getWaypoints().size(); i++) {
			System.out.println(" Next target: "+plane.getWaypoints().get(i));
			nodesStatus = createBreadcrumbMap(nodes, startNodes, starttime);
			
			result = findWaypoint(plane, nodesStatus, plane.getWaypoints().get(i), new ArrayDeque<Node>(startNodes));
			if (result != null) {
				// setze Start-Parameter für Suche zum nächsten Waypoint
				startNodes = Arrays.asList(result);
				starttime = nodesStatus.get(result).getTime();
			} else {
				System.out.println("Es wurde kein Weg gefunden :(");
				return false;
			}
		}
		System.out.println("Es wurde ein Weg gefunden!");
		return true;
	}
	
	/**
	 * Finde Pfad zu einem waypoint
	 * 
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param nodesStatus Informationen zum Pfad
	 * @param waypoint Gesuchter waypoint
	 * @param deq Deque für die Breitensuche (am Anfang mit Startnodes gefüllt)
	 * @return Ziel-Node wenn die Suche erfolgreich war, sonst null
	 */
	private static Node findWaypoint(Plane plane, Map<Node,Breadcrumb>nodesStatus, Targettype waypoint, Deque<Node> deq) 
	{
		Node current = deq.getFirst();							// in diesem Durchlauf zu überprüfender Node
		int currentTime = nodesStatus.get(current).getTime(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		
		// vergleiche, ob current der gesuchte waypoint ist
		if (current.getTargettype() != null &&	current.getTargettype().equals(waypoint)) {	//<ToDo: bis in alle Ewigkeit reservieren
			savePath(current,plane,nodesStatus);				
			return current; 
		}
		
		for(Node child: current.getTo()) {
			if(
				nodesStatus.get(child).getStatus()==Status.UNKNOWN &&         // falls Knoten noch nicht entdeckt und
				child.isFree(currentTime+1) && child.isFree(currentTime+2) 	  // zur Zeit fuer zwei Ticks nicht reserviert
				) {
					deq.addLast(child);
					nodesStatus.get(child).setStatus(Status.SPOTTED);		// Status auf entdeckt aendern
					nodesStatus.get(child).setTime(currentTime+1); 			// der Zeitpunkt, an dem der Node erreicht wird
					nodesStatus.get(child).setFrom(current);				// From ist der jetzige Knoten (da er ihn entdeckt hat)
				}
		}
		nodesStatus.get(current).setStatus(Status.DONE);					// alle Kinder-Knoten sind entdeckt, der Knoten kann 
		deq.removeFirst();													// auf "DONE" gesetzt und aus der Warteschlange geloescht werden
		
		if(deq.size()==0) return null;										// return null, wenn kein Weg gefunden werden kann
		else return findWaypoint(plane, nodesStatus, waypoint, deq);		// die Breitensuche fortsetzen

	}
	
	private static void savePath(Node node, Plane plane,Map<Node,Breadcrumb>nodesStatus) {
			int time = nodesStatus.get(node).getTime();
			node.putReserved(time, plane,true);
			node.putReserved(time +1, plane,false);
			if(nodesStatus.get(node).getFrom()!=null) {
				savePath(nodesStatus.get(node).getFrom(),plane,nodesStatus);
			}
			System.out.println("Node "+node.getName() +", Time: " + time);
	}
	
	private static Map<Node,Breadcrumb> createBreadcrumbMap(Collection<Node> nodes, Collection<Node> startNodes, Integer time){
		Map<Node,Breadcrumb> nodesStatus = new HashMap<Node,Breadcrumb>();
		
		for(Node node:nodes) {
			nodesStatus.put(node, new Breadcrumb()); 									// alle Nodes in die Map schreiben (als UNKNOWN)
			if(startNodes.contains(node)) nodesStatus.get(node).setTime(time); 			// fuer die Startnodes wird angefangen zu zaehlen
		}
		return nodesStatus;
	}
}