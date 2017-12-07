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
	 * Suche und reserviere einen Pfad für ein Flugzeug vom ersten Waypoint zum zweiten
	 * 
	 * @param nodes Die zu durchsuchenden Nodes
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	 * @return true wenn ein Pfad gefunden wurde, sonst false 
	 */
	public static boolean searchFirstWaypoint(Collection<Node> nodes, Plane plane, int starttime) {
		System.out.println("Suche waypoint: "+plane.getWaypoints().get(1));

		// find Nodes with the start target type that are free at starttime
		List<Node> startNodes = new ArrayList<>();	
		for (Node node: nodes) {
			if (node.getTargettype() != null && node.getTargettype().equals(plane.getWaypoints().get(0)) && node.isFree(starttime,plane)) {
				startNodes.add(node);
			}
		}
		
		// Wenn kein Startnode frei ist
		if (startNodes.isEmpty()) {
			System.out.println("Es ist kein Startnode frei :(");
			return false;
		}
		
		Map<Node,Breadcrumb> nodesStatus = createBreadcrumbMap(nodes, startNodes, starttime);
		
		return findWaypoint(plane, nodesStatus, plane.getWaypoints().get(1), new ArrayDeque<Node>(startNodes));
	}
	
	/**
	 * Suche und reserviere einen Pfad für ein Flugzeug von einem Node zu einem Waypoint
	 * 
	 * @param nodes Die zu durchsuchenden Nodes
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	 * @param startNode Node bei dem der Pfad starten soll
	 * @param targetWaypoint Ziel
	 * @return true wenn ein Pfad gefunden wurde, sonst false 
	 */
	public static boolean search(Collection<Node> nodes, Plane plane, int starttime, Node startNode, Targettype targetWaypoint) {
		System.out.println("Suche waypoint: ["+targetWaypoint+"]");

		List<Node> startNodes = Arrays.asList(startNode);
		Map<Node,Breadcrumb> nodesStatus = createBreadcrumbMap(nodes, startNodes, starttime);
		
		return findWaypoint(plane, nodesStatus, targetWaypoint, new ArrayDeque<Node>(startNodes));
	}
	
	/**
	 * Finde Pfad zu einem waypoint (rekursiv)
	 * 
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param nodesStatus Informationen zum Pfad
	 * @param waypoint Gesuchter waypoint
	 * @param deq Deque für die Breitensuche (am Anfang mit Startnodes gefüllt)
	 * @return true wenn ein Pfad gefunden wurde, sonst false
	 */
	private static boolean findWaypoint(Plane plane, Map<Node,Breadcrumb>nodesStatus, Targettype waypoint, Deque<Node> deq) {
		Node current = deq.getFirst();							// in diesem Durchlauf zu überprüfender Node
		Integer currentTime = nodesStatus.get(current).getTime(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		
		// vergleiche, ob current der gesuchte waypoint ist
		if (current.getTargettype() != null && current.getTargettype().equals(waypoint)) {
			savePath(current,plane,nodesStatus);					// Pfad reservieren
			boolean hasNextTarget = plane.increaseCurrentTarget();	// Nächsten Zielwaypoint setzen, falls vorhanden
			if (hasNextTarget) {
				current.setBlockedBy(plane,currentTime);						// Letzten Node dauerhaft blockieren wenn Endziel nicht erreicht
				System.out.println("Es wurde ein Weg zum nächsten waypoint ("+waypoint+") gefunden :)");
			} else {
				System.out.println("Es wurde ein Weg zum Endziel gefunden :)");
			}
			return true;
		}
		
		for(Node child: current.getTo()) {
			if(
				nodesStatus.get(child).getStatus()==Status.UNKNOWN &&         // falls Knoten noch nicht entdeckt und
				child.isFree(currentTime+1,plane) && child.isFree(currentTime+2,plane) // zur Zeit fuer zwei Ticks nicht reserviert
				) 
			{
				Targettype childTType = child.getTargettype();

				if(!(childTType!=null && childTType.equals(waypoint) && child.isBlocked())) { // falls das Kind (nicht (der gesuchte Waypoint ist && dabei geblockt ist)) || einfach ein "normales" child ist (!!)
					deq.addLast(child);
					nodesStatus.get(child).setStatus(Status.SPOTTED);		// Status auf entdeckt aendern
					nodesStatus.get(child).setTime(currentTime+1); 			// der Zeitpunkt, an dem der Node erreicht wird
					nodesStatus.get(child).setFrom(current);				// From ist der jetzige Knoten (da er ihn entdeckt hat)
				}
			}
		}
		nodesStatus.get(current).setStatus(Status.DONE);					// alle Kinder-Knoten sind entdeckt, der Knoten kann 
		deq.removeFirst();													// auf "DONE" gesetzt und aus der Warteschlange geloescht werden
		
		if(deq.size()==0) {
			System.out.println("Es wurde kein Weg gefunden :(");
			return false;													// return false, wenn kein Weg gefunden werden kann
		}
		else return findWaypoint(plane, nodesStatus, waypoint, deq);		// die Breitensuche fortsetzen

	}
	
	private static void savePath(Node node, Plane plane,Map<Node,Breadcrumb>nodesStatus) {
			int time = nodesStatus.get(node).getTime();
			node.putReserved(time, plane,true);
			node.putReserved(time +1, plane,false);
			if(nodesStatus.get(node).getFrom()!=null) {
				savePath(nodesStatus.get(node).getFrom(),plane,nodesStatus);
			}
			System.out.println("[Search] Node "+node.getName() +", Time: " + time );
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