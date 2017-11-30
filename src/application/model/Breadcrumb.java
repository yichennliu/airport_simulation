package application.model;

import java.util.HashMap;
import java.util.Map;

public class Breadcrumb {
	private Status status = Status.UNKNOWN;
	private Node from = null;
	private int time = 0;
	private Map<Integer, Status> statusMap = new HashMap<Integer,Status>();
	
	
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
