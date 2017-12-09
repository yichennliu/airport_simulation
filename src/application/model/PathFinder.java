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
		List<Breadcrumb> startNodes = new ArrayList<Breadcrumb>();	
		Map<Node,Breadcrumb> startLinkedBreadcrumbs  = new HashMap<Node,Breadcrumb>(); //Map f�r die Verkn�pfung der Breadcrumbs miteinander
		
		for (Node node: nodes) {
			//Wenn ein TargetType vorhanden && dieser dem ersten Waypoint des Planes entspricht && dieser Node nicht belegt ist
			if (node.getTargettype() != null && node.getTargettype().equals(plane.getWaypoints().get(0)) && node.isFree(starttime,plane)) {
				Breadcrumb newBreadcrumb = new Breadcrumb(Status.UNKNOWN, null, node, starttime);
				startNodes.add(newBreadcrumb);	
				startLinkedBreadcrumbs.put(node, newBreadcrumb);	
			}
		}
		
		// Wenn kein Startnode frei ist
		if (startNodes.isEmpty()) {
			System.out.println("Es ist kein Startnode frei :(");
			return false;
		}
		
		
		return findWaypoint(plane, plane.getWaypoints().get(1), new ArrayDeque<Breadcrumb>(startNodes),new HashMap<Node,Breadcrumb>());
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
		Breadcrumb startBreadcrumb = new Breadcrumb(Status.UNKNOWN, null, startNode, starttime);
		
		List<Breadcrumb> startDeq = Arrays.asList(startBreadcrumb);
		Map<Node,Breadcrumb> startLinkedBreadcrumbs  = new HashMap<Node,Breadcrumb>();
		startLinkedBreadcrumbs.put(startNode, startBreadcrumb);
		
		return findWaypoint(plane, targetWaypoint, new ArrayDeque<Breadcrumb>(startDeq),new HashMap<Node,Breadcrumb>());
	}
	
	/**
	 * Finde Pfad zu einem waypoint (rekursiv)
	 * 
	 * @param plane Das Fluzeug, fuer das die Suche durchefuehrt werden soll
	 * @param waypoint Gesuchter waypoint
	 * @param deq Deque für die Breitensuche (am Anfang mit Startnodes gefüllt)
	 * @param Map mit linkedBreadcrumbs zur Verkn�pfung der m�glichen Wegm�glichkeiten
	 * @return true wenn ein Pfad gefunden wurde, sonst false
	 */
	private static boolean findWaypoint(Plane plane, Targettype waypoint, Deque<Breadcrumb> deq, Map<Node, Breadcrumb> linkedBreadcrumbs) {
		Breadcrumb current = deq.getFirst();							// in diesem Durchlauf zu überprüfender Breadcrumb 
		Breadcrumb fromBreadcrumb = current.getFrom();
		Node currentNode = current.getPointsAt();
		Integer currentTime = current.getTime(); 	// holt aus NodesStatus die aktuelle Zeit seit dem ersten find()-Aufruf
		Targettype currentTargettype = currentNode.getTargettype();
		int count = 0;
		
		if(currentTargettype != null && currentTargettype.equals(waypoint)) {	//Pr�fen ob Ziel erreicht wurde
			savePath(current,plane);
			System.out.println("\n- - - - -");
			boolean hasNextTarget = true;
			if(!currentNode.getTargettype().equals(Targettype.wait)) { 	// falls der jetzige Node kein wait-Knoten ist.
				hasNextTarget = plane.increaseCurrentTarget();		// Nächsten Zielwaypoint setzen, falls vorhanden
			}
				
			if (hasNextTarget) {
				currentNode.setBlockedBy(plane,currentTime);						// Letzten Node dauerhaft blockieren wenn Endziel nicht erreicht
			} 
			return true;
		}
		
		
		
		if(fromBreadcrumb!=null) {
			count = fromBreadcrumb.getCounter(); 
			if(fromBreadcrumb.getPointsAt() == currentNode) {		// falls man schon am selben Node war, das hei�t der Node vom fromBreadcrumb == currentNode
				count++;
				current.setCounter(count);
			}
		}
		
		if (count<10) { // nur die Childs �berpr�fen, wenn Count nicht �berschritten
			
			Collection<Node> toList = currentNode.getTo();
			
			for(Node child: toList) {
				Breadcrumb childBreadcrumb = linkedBreadcrumbs.get(child);
				if((childBreadcrumb==null || (childBreadcrumb!=null && childBreadcrumb.getPointsAt() == currentNode)) && 					// wurde noch nicht entdeckt
						(child.isFree(currentTime+1, plane) &&	// ist frei
						child.isFree(currentTime+2, plane))) 
				{
					
						Targettype childTType = child.getTargettype();
						
						if(!(childTType!=null && childTType.equals(waypoint) && child.isBlocked())) { // falls das Kind (nicht (der gesuchte Waypoint ist && dabei geblockt ist)) (es ist also entweder nicht der gesuchte Waypoint, oder, wenn es einer ist, darf er nicht geblockt sein)
							Breadcrumb 	newBreadcrumb = new Breadcrumb(Status.SPOTTED ,current ,child ,currentTime+1);

							linkedBreadcrumbs.put(child, newBreadcrumb);
							deq.addLast(newBreadcrumb);
						}
				}
			}
			
			current.setStatus(Status.DONE);
			Kind nodeKind = currentNode.getKind();
			if(nodeKind == Kind.concrete || nodeKind == Kind.hangar) {
				if(currentNode.isFree(currentTime+1,plane) && currentNode.isFree(currentTime+2,plane)) {
					Breadcrumb 	newBreadcrumb = new Breadcrumb(Status.SPOTTED ,current ,currentNode ,currentTime+1);
					linkedBreadcrumbs.put(currentNode,newBreadcrumb);
					deq.addLast(newBreadcrumb);
				}
				
			}
		}
		deq.removeFirst();
		
		if(deq.size()==0) {
			System.out.println("Es wurde kein Weg gefunden :(");
			return false;													// return false, wenn kein Weg gefunden werden kann
		}
		
		else return findWaypoint(plane,waypoint,deq,linkedBreadcrumbs);
									
	}
				
	
	private static void savePath(Breadcrumb breadcrumb, Plane plane) {
			
			int time = breadcrumb.getTime();
			Breadcrumb fromBreadcrumb = breadcrumb.getFrom();
			Node node = breadcrumb.getPointsAt();
			node.putReserved(time, plane,true);
			
			if(!(fromBreadcrumb!=null && fromBreadcrumb.getPointsAt() == node))  node.putReserved(time +1, plane,false);	//Wenn der Node des fromBreadcrumbs gleich dem Node des currentVreadcrumbs entspricht, dann passiert nichts, weil wir gewartet haben. ABER: Falls das nicht der Fall ist, wir also nicht gewartet haben, dann auf True setzen.
			
			if(fromBreadcrumb!=null) {
				savePath(fromBreadcrumb,plane);
			}
			System.out.print("[Search for "+plane.toString()+"] Node "+node.getName() +", Time: " + time );
	}
	
}