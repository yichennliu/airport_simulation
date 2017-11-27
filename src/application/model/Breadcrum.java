package application.model;

public class Breadcrum {
	private Status status = Status.UNKNOWN;
	private Node from = null;
	private int time = 0;
	
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setFrom(Node from) {
		this.from = from;
	}
	
	public Node getFrom() {
		return this.from;
	}
}
