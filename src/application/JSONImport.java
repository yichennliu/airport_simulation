package application;

import java.io.*;
import java.util.*;

import org.json.*;

import application.model.*;

public class JSONImport {
	
	public static Flughafen createFlughafen(String jsonPath) throws JSONException {
		/* load file */
		InputStream is = ClassLoader.getSystemResourceAsStream(jsonPath);
		JSONObject json = new JSONObject(new JSONTokener(is));
		
		/* import maxplanes */
		int maxplanes = json.getInt("maxplanes");
		
		/* Import planes */
		ArrayList<Plane> planes = new ArrayList<Plane>();
		if (json.has("planes")) {
			JSONArray jsonPlanes = json.getJSONArray("planes");
			
			for (int i = 0; i < jsonPlanes.length(); i++) {
				JSONObject jsonPlane = (JSONObject) jsonPlanes.get(i);
				
				/* import wayoints */
				JSONArray jsonWaypoints = jsonPlane.getJSONArray("waypoints");
				List<Targettype> waypoints = new ArrayList<Targettype>();
				for (int j = 0; j < jsonWaypoints.length(); j++) {
					waypoints.add(Targettype.valueOf((String) jsonWaypoints.get(j)));
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
				JSONObject jsonGenerator = (JSONObject) jsonGenerators.get(i);
				
				/* import wayoints */
				JSONArray jsonWaypoints = jsonGenerator.getJSONArray("waypoints");
				List<Targettype> waypoints = new ArrayList<Targettype>();
				for (int j = 0; j < jsonWaypoints.length(); j++) {
					waypoints.add(Targettype.valueOf(jsonWaypoints.getString(j)));
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
			JSONObject jsonNode = (JSONObject) jsonNodes.get(i);
			
			/* import coordinates */
			double x = jsonNode.getDouble("x");
			double y = jsonNode.getDouble("y");
			
			/* import name */
			String name = jsonNode.getString("name");
			
			/* import kind */
			Kind kind = Kind.valueOf(jsonNode.getString("kind"));
			
			/* for "to" and "conflicts" all nodes have to be created first */
			
			/* import (optional) targettype */
			Targettype targettype = null;
			if (jsonNode.has("targettype")) {
				targettype = Targettype.valueOf(jsonNode.getString("targettype"));
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
			JSONObject jsonNode = (JSONObject) jsonNodes.get(i);
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
	}
}
