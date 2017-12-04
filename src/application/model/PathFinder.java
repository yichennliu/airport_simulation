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
	 * @param nodes Die zu durchsuchenden Nodes
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	 * @param tTypeStart Der Targettype des Startpunkts der Suche
	 * @param tTypeEnd Der Targettype des Endpunkts der Suche
	*/
	public static void startSearch(Collection<Node> nodes, Plane plane,int starttime, Targettype tTypeStart, Targettype tTypeEnd) {
		// find Nodes with the start target type that are free at starttime
		List<Node> startNodes = new ArrayList<>();	
		for (Node node: nodes) {
			if (node.getTargettype() != null && node.getTargettype().equals(tTypeStart) && node.isFree(starttime)) {
				startNodes.add(node);
			}
		}
			
		Map<Node,Breadcrumb> nodesStatus = createBreadcrumbMap(nodes, startNodes, starttime);

		if(find(plane, nodesStatus, tTypeEnd, new ArrayDeque<Node>(startNodes)))
			System.out.println("Es wurde ein Weg gefunden!");
		else System.out.println("Es wurde kein Weg gefunden :(");
	}

	/**
	 * @param nodes Die zu durchsuchenden Nodes
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	 * @param startNode Der Startnode der Suche
	 * @param tTypeEnd Der Targettype des Endpunkts der Suche
	*/
	public static void startSearch(Collection<Node> nodes, Plane plane, int starttime, Node startNode, Targettype tTypeEnd) {
		Map<Node,Breadcrumb> nodesStatus = createBreadcrumbMap(nodes, Arrays.asList(startNode), starttime);
		if(find(plane, nodesStatus, tTypeEnd, new ArrayDeque<Node>(Arrays.asList(startNode))))
			System.out.println("Es wurde ein Weg gefunden!");
		else System.out.println("Es wurde kein Weg gefunden :(");
	}
	
	/**
	 * @return gibt true zurueck, falls ein Weg gefunden wurde, andernfalls false
	 */
	private static boolean find(Plane plane, Map<Node,Breadcrumb>nodesStatus, Targettype tTypeEnd, Deque<Node> deq) 
	{
		Node current = deq.getFirst();							// in diesem Durchlauf zu überprüfender Node
		int currentTime = nodesStatus.get(current).getTime(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		
		// vergleiche ob current der letzte waypoint ist
		if (current.getTargettype() != null &&	current.getTargettype().equals(tTypeEnd)) {	//<ToDo: bis in alle Ewigkeit reservieren
			savePath(current,plane,nodesStatus);				
			return true; 
		}
		
		for(Node child: current.getTo()) {
			if(
				nodesStatus.get(child).getStatus()==Status.UNKNOWN &&         // falls Knoten noch nicht entdeckt und
				child.isFree(currentTime+1) && child.isFree(currentTime+2) 	  // zur Zeit fuer zwei Ticks nicht reserviert
				) {
					deq.addLast(child);
					nodesStatus.get(child).setStatus(Status.SPOTTED);	// Status auf entdeckt aendern
					nodesStatus.get(child).setTime(currentTime+1); 		// der Zeitpunkt, an dem der Node erreicht wird
					nodesStatus.get(child).setFrom(current);			// From ist der jetzige Knoten (da er ihn entdeckt hat)
				}
		}
		nodesStatus.get(current).setStatus(Status.DONE);				// alle Kinder-Knoten sind entdeckt, der Knoten kann 
		deq.removeFirst();												// auf "DONE" gesetzt und aus der Warteschlange geloescht werden
		
		if(deq.size()==0) return false;									// return false, wenn kein Weg gefunden werden kann
		else return find(plane, nodesStatus, tTypeEnd, deq);			// die Breitensuche fortsetzen

	}
	
	private static void savePath(Node node, Plane plane,Map<Node,Breadcrumb>nodesStatus) {
			int time = nodesStatus.get(node).getTime();
			node.putReserved(time, plane);
			node.putReserved(time +1, plane);
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