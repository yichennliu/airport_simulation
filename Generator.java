


public class Generator {
	private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	private double chance;
	
	public Generator(ArrayList<Waypoint> waypoints, double chance) {
		this.chance = chance;
		this.waypoints = waypoints;
		
	}
	
	public Plane execute() { 
		if( chance<=Math.random()) {
			return new Plane(this.waypoints, Flughafen.getTime());
		} else{
			return null; 
		}
	}
	
}
