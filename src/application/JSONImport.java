package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import application.model.Flughafen;
import application.model.Generator;
import application.model.Kind;
import application.model.Node;
import application.model.Plane;
import application.model.Targettype;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JSONImport {
	
	public static Flughafen createFlughafen(String jsonPath) {
		try {
			/* load file with relative path*/
			InputStream is = ClassLoader.getSystemResourceAsStream(jsonPath); 
			if(is==null) {
				/*if file not found try to load file with absolute path*/
				try {
					is = new FileInputStream(jsonPath);
				}
				catch(FileNotFoundException e) {
					throw new JSONException("File not found");
				}
			}
			JSONObject json = new JSONObject(new JSONTokener(is));
			
			/* import maxplanes */
			int maxplanes = json.getInt("maxplanes");
			
			/* Import planes */
			ArrayList<Plane> planes = new ArrayList<Plane>();
			if (json.has("planes")) {
				JSONArray jsonPlanes = json.getJSONArray("planes");
				
				for (int i = 0; i < jsonPlanes.length(); i++) {
					JSONObject jsonPlane = jsonPlanes.getJSONObject(i);
					
					/* import wayoints */
					JSONArray jsonWaypoints = jsonPlane.getJSONArray("waypoints");
					List<Targettype> waypoints = new ArrayList<Targettype>();
					for (int j = 0; j < jsonWaypoints.length(); j++) {
						waypoints.add(Targettype.valueOf(jsonWaypoints.getString(j).toUpperCase()));
					}
					
					/* Import inittime */
					int inittime = jsonPlane.getInt("inittime");
					
					/* create + add plane */
					planes.add(new Plane(waypoints, inittime));
				}
			}
			
			/* Import generators */
			ArrayList<Generator> generators = new ArrayList<Generator>();
			if (json.has("generators")) {
				JSONArray jsonGenerators = json.getJSONArray("generators");
				
				for (int i = 0; i < jsonGenerators.length(); i++) {
					JSONObject jsonGenerator = jsonGenerators.getJSONObject(i);
					
					/* import wayoints */
					JSONArray jsonWaypoints = jsonGenerator.getJSONArray("waypoints");
					List<Targettype> waypoints = new ArrayList<Targettype>();
					for (int j = 0; j < jsonWaypoints.length(); j++) {
						waypoints.add(Targettype.valueOf(jsonWaypoints.getString(j).toUpperCase()));
					}
					
					/* Import chance */
					double chance = jsonGenerator.getDouble("chance");
					
					/* create + add generator */
					generators.add(new Generator(waypoints, chance));
				}
			}
			
			/* Import nodes */
			TreeMap<String, Node> nodes = new TreeMap<String, Node>();
			JSONArray jsonNodes = json.getJSONArray("nodes");
			
			for (int i = 0; i < jsonNodes.length(); i++) {
				JSONObject jsonNode = jsonNodes.getJSONObject(i);
				
				/* import coordinates */
				double x = jsonNode.getDouble("x");
				double y = jsonNode.getDouble("y");
				
				/* import name */
				String name = jsonNode.getString("name");
				
				/* import kind */
				Kind kind = Kind.valueOf(jsonNode.getString("kind").toUpperCase());
				
				/* for "to" and "conflicts" all nodes have to be created first */
				
				/* import (optional) targettype */
				Targettype targettype = null;
				if (jsonNode.has("targettype")) {
					targettype = Targettype.valueOf(jsonNode.getString("targettype").toUpperCase());
				}
				
				/* import (optional) waittime */
				int waittime = 0;
				if (jsonNode.has("waittime")) {
					waittime = jsonNode.getInt("waittime");
				}
				
				/* create + add node (with empty "to" and "conflicts" field) */
				nodes.put(name, new Node(x, y, name, kind, new TreeMap<String, Node>(),
						new TreeMap<String, Node>(), targettype, waittime));
			}
			
			/* fill "to"/"conflicts" connections after above loop created all nodes */
			for (int i = 0; i < jsonNodes.length(); i++) {
				JSONObject jsonNode = jsonNodes.getJSONObject(i);
				Node node = nodes.get(jsonNode.getString("name"));
				
				/* import to */
				JSONArray jsonTo = jsonNode.getJSONArray("to");
				for (int j = 0; j < jsonTo.length(); j++) {
					node.addToNode(nodes.get(jsonTo.getString(j)));
				}
				
				/* import conflicts */
				JSONArray jsonConflicts = jsonNode.optJSONArray("conflicts");
				if (jsonConflicts != null) {
					for (int j = 0; j < jsonConflicts.length(); j++) {
						node.addConflict(nodes.get(jsonConflicts.getString(j)));
					}
				}
			}
			
			return new Flughafen(maxplanes, planes, generators, nodes);
			
		} catch (Exception importErrror) {
			// Zeige Fehler wenn Import scheitert
	    	Alert alertWindow = new Alert(Alert.AlertType.ERROR, importErrror.toString(), ButtonType.OK);
	    	String noEnumError = "No enum constant application.model.Targettype.";
	    	if (importErrror.getMessage().startsWith(noEnumError)) {
	    		alertWindow.setHeaderText("Targettype \"" + importErrror.getMessage().split(Pattern.quote(noEnumError))[1] + "\" wurde nicht gefunden");
	    	} else {
	    		alertWindow.setHeaderText("Fehler beim Import, bitte überprüfen Sie ihre JSON-Datei");
	    	}
	    	Image img = new Image("/application/source/Images/ber.jpg");
	    	alertWindow.setGraphic(new ImageView(img));
	    	alertWindow.show();
	    	return null;
	    }
	}
}
