package application.model;

public class PathTicket {
	
	private Node node;
	private int ankunftszeit;
	private int abflugzeit;
		
		
	public PathTicket(Node node, int ankunftszeit, int abflugzeit) {
		this.node = node;
		this.ankunftszeit = ankunftszeit;
		this.abflugzeit = abflugzeit;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public int getAnkunftszeit() {
		return this.ankunftszeit;
	}
	
	public int getAbflugzeit() {
		return this.abflugzeit;
	}
	
}
