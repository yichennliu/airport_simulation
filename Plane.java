import java.util.ArrayList;
import java.util.List;

public class Plane {

	private List<Waypoint>waypoints = new ArrayList<Waypoint>();
	private int inittime;
	
	public Plane(List<Waypoint> waypoints, int inittime) {
		this.waypoints = waypoints;
		this.inittime = inittime;
	}
	
}
