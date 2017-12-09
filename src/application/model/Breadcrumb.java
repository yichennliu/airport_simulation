package application.model;

public class Breadcrumb {
	private Status status = Status.UNKNOWN;
	private Breadcrumb from = null;
	private int counter = 0;
	private int time = 0;
	private Node pointsAt = null;
	
	
	public Breadcrumb (Status status, Breadcrumb from, Node pointsAt, int time) {
		
		this.status = status;
		this.from = from;
		this.pointsAt = pointsAt;
		this.time = time;
	}
	
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public int getCounter() {
		return this.counter;
	}
	
	public void setCounter(int count) {
		this.counter=count;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setFrom(Breadcrumb from) {
		this.from = from;
	}  
	
	public Breadcrumb getFrom() {
		return this.from;
	}
	
	public void setPointsAt(Node node) {
		this.pointsAt = node;
	}
	
	public Node getPointsAt() {
		return this.pointsAt;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getTime() {
		return this.time;
	}
	
}
