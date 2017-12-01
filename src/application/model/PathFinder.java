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
	 * @param start Der Startpunkt der Suche
	 * @param end Der Endpunkt der Suche
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	*/
	public static void startSearch(Collection<Node> nodes, Plane plane,int starttime) {
		// find Nodes with the start target type
		Targettype startType = plane.getWaypoints().get(0);
		
		List<Node> startNodes = new ArrayList<>();
		
		for (Node node: nodes) {
			if (node.getTargettype() != null && node.getTargettype().equals(startType)) {
				startNodes.add(node);
			}
		}
		
		// set the first free node with start target type as start node
		Node start = null;
		for (Node startNode: startNodes) {
			if (startNode.isFree(starttime)) {
				start = startNode;
			}
		}
		
		Map<Node,Breadcrumb> nodesStatus = new HashMap<Node,Breadcrumb>(); // verknuepft Nodes mit der Information, ob und Wie sie besucht wurden
		for(Node node:nodes) {
			nodesStatus.put(node, new Breadcrumb()); // alle Nodes in die Map schreiben (als UNKNOWN)
			if(node==start) nodesStatus.get(node).setTime(starttime); // fuer den Startnode wird angefangen zu zaehlen
		}
		if(find(start,start,plane,nodesStatus,new ArrayDeque<Node>(Arrays.asList(start))))
			System.out.println("Es wurde ein Weg gefunden!");
		else System.out.println("Es wurde kein Weg gefunden :(");
	}
	
	/**
	 * @return gibt true zurueck, falls ein Weg gefunden wurde, andernfalls false
	 */
	private static boolean find(Node start, 
		Node current, Plane plane, Map<Node,Breadcrumb>nodesStatus, 
								Deque<Node> deq) 
	{
		int currentTime = nodesStatus.get(current).getTime(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		
		// vergleiche ob current der letzte waypoint ist
		if (current.getTargettype() != null && 
				current.getTargettype().equals(plane.getWaypoints()
						.get(plane.getWaypoints().size()-1))) {	//<ToDo: bis in alle Ewigkeit reservieren. UNd BEdingung: falls end nicht belegt ist>
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
		deq.removeFirst();												// auf "DONE" gesetzt und aus der Warteschlange gel�scht werden
		
		if(deq.size()==0) return false;										// return false, wenn kein Weg gefunden werden kann
		else return find(start,deq.peekFirst(),plane,nodesStatus,deq);  // die Breitensuche fortsetzen

	}
	
	private static void savePath(Node node, Plane plane,Map<Node,Breadcrumb>nodesStatus) {
		int time;
		while(node!=null) {
			time = nodesStatus.get(node).getTime();
			System.out.println("Node "+node.getName() +", Time: " + time);
			node = nodesStatus.get(node).getFrom();	
		}
	}
	
}