package application.model;

public enum Status {
	UNKNOWN,SPOTTED,DONE;
	private Node from = null;
	private int time=0;
	
	public Node from() {
		return this.from;
	}
	
	public void setFrom(Node from) {
		this.from = from;
	}
	
	public int time() {
		return this.time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
}
