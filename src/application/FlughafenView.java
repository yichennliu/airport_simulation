package application;

import application.model.*;
import application.model.Node;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FlughafenView {
	private Flughafen model;
	private Stage stage;
	private Scene scene;
	
	private Canvas canvas;
	private GraphicsContext gc;
	private final int HEIGHT = 600;
	private final int WIDTH = 800;
	
	private double zoomFactor = 1.0;
	private double offsetX = 0.0;
	private double offsetY = 0.0;

	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		
		Group root = new Group();
		this.canvas = new Canvas(WIDTH,HEIGHT);
		this.gc = canvas.getGraphicsContext2D();
		
		root.getChildren().add(canvas);
		
		this.setInitialZoomAndOffset(model.getNodes());
		
		this.scene = new Scene(root);
		this.stage.setScene(scene);
		this.stage.setTitle("Flughafen");
		this.stage.show();
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Canvas getCanvas() {
		return this.canvas;
	}
	
	public void drawCanvas(){
		gc.clearRect(0, 0, this.WIDTH, this.HEIGHT);
		Collection<Node> nodes = model.getNodes();
		List<Plane> planes = model.getPlanes();
		drawNodes(nodes);
	}
	

	
	private void drawNodes(Collection <Node> nodes) {
		for(Node node:nodes) drawNode(node);
	}
	private void drawNode(Node node) {
		double radius = 5;
		double x = (node.getX()*this.zoomFactor)+offsetX;
		double y = (node.getY()*this.zoomFactor)+offsetY;
		Kind kind = node.getKind();
		this.gc.setLineWidth(1.0);
		this.gc.setStroke(Color.DARKGREY);
		this.gc.setFill(Color.GREY);
		switch(kind) {
			case air: {
				this.gc.setStroke(Color.BLUE);
				this.gc.setLineWidth(0.2);
				this.gc.setFill(Color.BLUE.darker());
				break;
			}
			
		}

		
		for(Node children: node.getTo()) {
			gc.strokeLine(x, y, (children.getX()*zoomFactor)+offsetX, (children.getY()*zoomFactor)+offsetY);
		}
		
		this.gc.fillOval(x-radius,y-radius,radius*2,radius*2);
	
	}
	
	
	private void setInitialZoomAndOffset(Collection <Node> nodes) { // setzt den initialen Faktor und Verschiebung, sodass alles auf das canvas passt; 
		Iterator <Node> it = nodes.iterator();
		if(it.hasNext()) {
			double minY, minX,maxX,maxY,widthFlughafen,heightFlughafen;
			Node firstNode = it.next();
			minY = firstNode.getX();
			minX = firstNode.getY();
			maxY = minY;
			maxX = minX;
			while(it.hasNext()) {
				Node currentNode = it.next();
				double currentX = currentNode.getX();
				double currentY = currentNode.getY();
				if (currentX<minX) minX = currentX;
				if (currentY<minY) minY = currentY;
				if (currentX>maxX) maxX = currentX;
				if (currentY>maxY) maxY = currentY;
			}
			maxX = maxX -minX; //maxX ist jetzt die breite des Flughafens
			maxY = maxY -minY; // maxY ist jetzt die Höhe des Flughafens
			
			if(maxY*((double) this.WIDTH/this.HEIGHT)<=maxX) { // passt den Flughafen in die Bildschirmmaße ein (orientiert an X)
				this.zoomFactor = this.WIDTH/maxX;
			}
			else this.zoomFactor = this.HEIGHT/maxY;
			
			widthFlughafen = maxX*this.zoomFactor; 
			heightFlughafen = maxY*this.zoomFactor;
			
			this.offsetX = (0 - minX*this.zoomFactor)+(this.WIDTH-(widthFlughafen))*0.5; // horizontalAlign
			this.offsetY = (0 - minY*this.zoomFactor)+(this.HEIGHT-(heightFlughafen))*0.5; // verticalAlign
		}
	}

	public double getZoomFactor() {
		return this.zoomFactor;
	}
	
	public void setZoomFactor(double factor) {
		this.zoomFactor = factor;
	}
}