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

	public static void startSearch(Collection<Node> nodes, Node start, Node end, Plane plane,int starttime) {
		Map<Node,Status> nodesStatus = new HashMap<Node,Status>(); // verknüpft Nodes mit der Information, ob und Wie sie besucht wurden
		for(Node node:nodes) {
			nodesStatus.put(node, Status.UNKNOWN); // alle Nodes in die Map schreiben (als UNKNOWN)
			if(node==start) nodesStatus.get(node).setTime(starttime); // für den Startnode wird angefangen zu zählen
		}
		find(start,end,null,start,plane,nodesStatus,new ArrayDeque<Node>(Arrays.asList(start)));
	}
	
	private static boolean find(Node start, Node end, Node from, Node current, Plane plane, Map<Node,Status>nodesStatus, Deque<Node> deq) {
		int currentTime = nodesStatus.get(current).time(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		if(current == end) {								//Todo: bis in alle Ewigkeit reservieren. UNd BEdingung: falls end nihct belegt ist
			current.putReserved(currentTime, plane);
			return true;
		}	
		for(Node child: current.getTo()) {
			if(
				nodesStatus.get(child)==Status.UNKNOWN && // falls Knoten noch nicht entdeckt und
				child.getReserved().get(currentTime+1)==null && // zur Zeit für zwei Ticks nicht reserviert
				child.getReserved().get(currentTime+2)==null // toDo: auf Conflicts checken (über MEthode hasConflicts(Node,time)
			) {
				deq.addLast(child);
				nodesStatus.put(child, Status.SPOTTED);
				nodesStatus.get(child).setTime(currentTime+1); // der Zeitpunkt, an dem der Node erreicht wird
			}
		}
		
		nodesStatus.put(current,Status.DONE);
		nodesStatus.get(current).setTime(currentTime);
		deq.removeFirst();
		
		if(find(start,end,current,deq.peekFirst(),plane,nodesStatus,deq)) {
			current.putReserved(currentTime, plane);
			System.out.println("Node "+current.getName() +", Time: " + currentTime);
			return true;
		}
		else return false;
	}
}