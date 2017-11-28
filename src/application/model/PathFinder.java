package application.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Deque;


public class PathFinder {
	
	/**
	 * @param nodes Die zu durchsuchenden Nodes
	 * @param start Der Startpunkt der Suche
	 * @param end Der Endpunkt der Suche
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param starttime Die Zeit im Modell, bei der die Suche losgehen soll
	*/
	public static void startSearch(Collection<Node> nodes, Node start, Node end, Plane plane,int starttime) {
		Map<Node,Breadcrumb> nodesStatus = new HashMap<Node,Breadcrumb>(); // verknï¿½pft Nodes mit der Information, ob und Wie sie besucht wurden
		for(Node node:nodes) {
			nodesStatus.put(node, new Breadcrumb()); // alle Nodes in die Map schreiben (als UNKNOWN)
			if(node==start) nodesStatus.get(node).setTime(starttime); // fï¿½r den Startnode wird angefangen zu zï¿½hlen
		}
		if(find(start,end, start,plane,nodesStatus,new ArrayDeque<Node>(Arrays.asList(start))))
			System.out.println("Es wurde ein Weg gefunden!");
		else System.out.println("Es wurde kein Weg gefunden :(");
	}
	
	/**
	 * @return gibt true zurueck, falls ein Weg gefunden wurde, andernfalls false
	 */
	private static boolean find(Node start, Node end, 
		Node current, Plane plane, Map<Node,Breadcrumb>nodesStatus, 
								Deque<Node> deq) 
	{
		int currentTime = nodesStatus.get(current).getTime(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		if(current == end) {									//<ToDo: bis in alle Ewigkeit reservieren. UNd BEdingung: falls end nicht belegt ist>
			savePath(current,plane,nodesStatus);				
			return true;
		}	
		for(Node child: current.getTo()) {
			if(
				nodesStatus.get(child).getStatus()==Status.UNKNOWN &&         // falls Knoten noch nicht entdeckt und
				child.isFree(currentTime+1) && child.isFree(currentTime+2) 	  // zur Zeit fÃ¼r zwei Ticks nicht reserviert
				) {
					deq.addLast(child);
					nodesStatus.get(child).setStatus(Status.SPOTTED);	// Status auf entdeckt aendern
					nodesStatus.get(child).setTime(currentTime+1); 		// der Zeitpunkt, an dem der Node erreicht wird
					nodesStatus.get(child).setFrom(current);			// From ist der jetzige Knoten (da er ihn entdeckt hat)
				}
		}
		nodesStatus.get(current).setStatus(Status.DONE);				// alle Kinder-Knoten sind entdeckt, der Knoten kann 
		deq.removeFirst();												// auf "DONE" gesetzt und aus der Warteschlange gelöscht werden
		
		if(deq.size()==0) return false;										// return false, wenn kein Weg gefunden werden kann
		else return find(start,end,deq.peekFirst(),plane,nodesStatus,deq);  // die Breitensuche fortsetzen

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